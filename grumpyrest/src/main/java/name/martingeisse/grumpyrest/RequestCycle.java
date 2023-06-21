/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest;

import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import name.martingeisse.grumpyjson.ExceptionMessages;
import name.martingeisse.grumpyjson.JsonGenerationException;
import name.martingeisse.grumpyjson.JsonValidationException;
import name.martingeisse.grumpyrest.request.PathArgument;
import name.martingeisse.grumpyrest.request.Request;
import name.martingeisse.grumpyrest.request.path.PathSegment;
import name.martingeisse.grumpyrest.request.path.PathUtil;
import name.martingeisse.grumpyrest.request.path.VariablePathSegment;
import name.martingeisse.grumpyrest.request.querystring.QuerystringParsingException;
import name.martingeisse.grumpyrest.response.FinishRequestException;
import name.martingeisse.grumpyrest.response.Response;
import name.martingeisse.grumpyrest.response.ResponseTransmitter;
import name.martingeisse.grumpyrest.response.standard.StandardErrorResponse;
import name.martingeisse.grumpyrest.servlet.RequestPathSourcingStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Hold the run-time state of processing a single request. Application code will normally not have to deal with a
 * request cycle directly, but rather implement a {@link SimpleHandler} that takes a {@link Request} and returns either
 * a {@link Response}, or a value that gets converted to such a response. Only if this scheme is not flexible enough
 * will the application implement a {@link ComplexHandler} and deal with the request cycle directly.
 */
public final class RequestCycle {

    private final RestApi api;
    private final HttpServletRequest servletRequest;
    private final HttpServletResponse servletResponse;
    private final List<String> pathSegments;
    private Route matchedRoute;
    private List<PathArgument> pathArguments;

    private final Request highlevelRequest;
    private final ResponseTransmitter responseTransmitter;

    public RequestCycle(
            RestApi api,
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse,
            RequestPathSourcingStrategy requestPathSourcingStrategy
    ) {
        this.api = api;
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;

        String pathText = requestPathSourcingStrategy.getPath(servletRequest);
        if (pathText == null) {
            this.pathSegments = List.of();
        } else {
            this.pathSegments = List.of(PathUtil.splitIntoSegments(pathText));
        }

        this.highlevelRequest = new MyRequest();
        this.responseTransmitter = new MyResponseTransmitter();
    }

    public RestApi getApi() {
        return api;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    public List<String> getPathSegments() {
        return pathSegments;
    }

    public Route getMatchedRoute() {
        return matchedRoute;
    }

    public Request getHighlevelRequest() {
        return highlevelRequest;
    }

    public ResponseTransmitter getResponseTransmitter() {
        return responseTransmitter;
    }

    void setMatchedRoute(Route matchedRoute) {
        Objects.requireNonNull(matchedRoute, "matchedRoute");

        List<PathSegment> matchedRouteSegments = matchedRoute.path().segments();
        if (matchedRouteSegments.size() != pathSegments.size()) {
            throw new IllegalArgumentException("matched route has different number of segments than the path of this request cycle");
        }

        List<PathArgument> newPathArguments = new ArrayList<>();
        for (int i = 0; i < pathSegments.size(); i++) {
            PathSegment routeSegment = matchedRouteSegments.get(i);
            if (routeSegment instanceof VariablePathSegment variable) {
                newPathArguments.add(new PathArgument(variable.getVariableName(), pathSegments.get(i), api.getFromStringParserRegistry()));
            }
        }

        this.matchedRoute = matchedRoute;
        this.pathArguments = List.copyOf(newPathArguments);
    }

    public <T> T parseBody(Class<T> clazz) {
        try {
            return api.getJsonEngine().parse(prepareParse(), clazz);
        } catch (JsonValidationException e) {
            throw new FinishRequestException(StandardErrorResponse.requestBodyValidationFailed(e));
        }
    }

    public <T> T parseBody(TypeToken<T> typeToken) {
        try {
            return api.getJsonEngine().parse(prepareParse(), typeToken);
        } catch (JsonValidationException e) {
            throw new FinishRequestException(StandardErrorResponse.requestBodyValidationFailed(e));
        }
    }

    public Object parseBody(Type type) {
        try {
            return api.getJsonEngine().parse(prepareParse(), type);
        } catch (JsonValidationException e) {
            throw new FinishRequestException(StandardErrorResponse.requestBodyValidationFailed(e));
        }
    }

    private InputStream prepareParse() {
        String contentType = servletRequest.getContentType();
        if (contentType == null || !contentType.equals("application/json")) {
            throw new FinishRequestException(StandardErrorResponse.JSON_EXPECTED);
        }
        try {
            return servletRequest.getInputStream();
        } catch (IOException e) {
            throw new FinishRequestException(StandardErrorResponse.IO_ERROR);
        }
    }

    public List<PathArgument> getPathArguments() {
        if (pathArguments == null) {
            throw new IllegalStateException("no route matched yet");
        }
        return pathArguments;
    }

    public <T> T parseQuerystring(Class<T> clazz) throws QuerystringParsingException {
        return clazz.cast(parseQuerystring((Type)clazz));
    }

    public <T> T parseQuerystring(TypeToken<T> typeToken) throws QuerystringParsingException {
        //noinspection unchecked
        return (T)parseQuerystring(typeToken.getType());
    }

    public Object parseQuerystring(Type type) throws QuerystringParsingException {
        Map<String, String[]> querystringMulti = servletRequest.getParameterMap();
        Map<String, String> querystringSingle = new HashMap<>();
        Map<String, String> errorMap = new HashMap<>();
        for (Map.Entry<String, String[]> entry : querystringMulti.entrySet()) {
            String[] values = entry.getValue();
            for (String value : values) {
                if (querystringSingle.put(entry.getKey(), value) != null) {
                    errorMap.put(entry.getKey(), ExceptionMessages.DUPLICATE_PARAMETER);
                }
            }
        }
        Object result = null;
        QuerystringParsingException originalException = null;
        try {
            result = api.getQuerystringParserRegistry().getParser(type).parse(querystringSingle, type);
            if (result == null) {
                throw new QuerystringParsingException(Map.of("(root)", "querystring parser returned null"));
            }
        } catch (QuerystringParsingException e) {
            originalException = e;
            // duplicate-parameter errors take precedence here
            for (Map.Entry<String, String> entry : e.getFieldErrors().entrySet()) {
                errorMap.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }
        if (!errorMap.isEmpty()) {
            throw new QuerystringParsingException(Map.copyOf(errorMap));
        }
        if (result == null) {
            // this can only happen if the originalException did not contain any errors
            throw originalException;
        }
        return result;
    }

    private final class MyResponseTransmitter implements ResponseTransmitter {

        @Override
        public void setStatus(int status) {
            servletResponse.setStatus(status);
        }

        @Override
        public void setContentType(String contentType) {
            servletResponse.setContentType(contentType);
        }

        @Override
        public void addCustomHeader(String name, String value) {
            servletResponse.addHeader(name, value);
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return servletResponse.getOutputStream();
        }

        @Override
        public void writeJson(Object value) throws JsonGenerationException, IOException {
            api.getJsonEngine().writeTo(value, servletResponse.getOutputStream());
        }

        @Override
        public void writeJson(Object value, TypeToken<?> typeToken) throws JsonGenerationException, IOException {
            api.getJsonEngine().writeTo(value, typeToken, servletResponse.getOutputStream());
        }

        @Override
        public void writeJson(Object value, Type type) throws JsonGenerationException, IOException {
            api.getJsonEngine().writeTo(value, type, servletResponse.getOutputStream());
        }

    }

    private class MyRequest implements Request {

        public List<PathArgument> getPathArguments() {
            if (pathArguments == null) {
                throw new IllegalStateException("no route matched yet");
            }
            return pathArguments;
        }

        public Object parseQuerystring(Type type) throws QuerystringParsingException {
            Map<String, String[]> querystringMulti = servletRequest.getParameterMap();
            Map<String, String> querystringSingle = new HashMap<>();
            Map<String, String> errorMap = new HashMap<>();
            for (Map.Entry<String, String[]> entry : querystringMulti.entrySet()) {
                String[] values = entry.getValue();
                for (String value : values) {
                    if (querystringSingle.put(entry.getKey(), value) != null) {
                        errorMap.put(entry.getKey(), ExceptionMessages.DUPLICATE_PARAMETER);
                    }
                }
            }
            Object result = null;
            QuerystringParsingException originalException = null;
            try {
                result = api.getQuerystringParserRegistry().getParser(type).parse(querystringSingle, type);
                if (result == null) {
                    throw new QuerystringParsingException(Map.of("(root)", "querystring parser returned null"));
                }
            } catch (QuerystringParsingException e) {
                originalException = e;
                // duplicate-parameter errors take precedence here
                for (Map.Entry<String, String> entry : e.getFieldErrors().entrySet()) {
                    errorMap.putIfAbsent(entry.getKey(), entry.getValue());
                }
            }
            if (!errorMap.isEmpty()) {
                throw new QuerystringParsingException(Map.copyOf(errorMap));
            }
            if (result == null) {
                // this can only happen if the originalException did not contain any errors
                throw originalException;
            }
            return result;
        }

        public <T> T parseBody(Class<T> clazz) {
            try {
                return api.getJsonEngine().parse(prepareParse(), clazz);
            } catch (JsonValidationException e) {
                throw new FinishRequestException(StandardErrorResponse.requestBodyValidationFailed(e));
            }
        }

        public <T> T parseBody(TypeToken<T> typeToken) {
            try {
                return api.getJsonEngine().parse(prepareParse(), typeToken);
            } catch (JsonValidationException e) {
                throw new FinishRequestException(StandardErrorResponse.requestBodyValidationFailed(e));
            }
        }

        public Object parseBody(Type type) {
            try {
                return api.getJsonEngine().parse(prepareParse(), type);
            } catch (JsonValidationException e) {
                throw new FinishRequestException(StandardErrorResponse.requestBodyValidationFailed(e));
            }
        }

        private InputStream prepareParse() {
            String contentType = servletRequest.getContentType();
            if (contentType == null || !contentType.equals("application/json")) {
                throw new FinishRequestException(StandardErrorResponse.JSON_EXPECTED);
            }
            try {
                return servletRequest.getInputStream();
            } catch (IOException e) {
                throw new FinishRequestException(StandardErrorResponse.IO_ERROR);
            }
        }


        public <T> T parseQuerystring(Class<T> clazz) throws QuerystringParsingException {
            return clazz.cast(parseQuerystring((Type)clazz));
        }

        public <T> T parseQuerystring(TypeToken<T> typeToken) throws QuerystringParsingException {
            //noinspection unchecked
            return (T)parseQuerystring(typeToken.getType());
        }

    }

}
