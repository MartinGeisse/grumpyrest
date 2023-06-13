/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import name.martingeisse.grumpyjson.ExceptionMessages;
import name.martingeisse.grumpyjson.JsonGenerationException;
import name.martingeisse.grumpyjson.JsonValidationException;
import name.martingeisse.grumpyrest.responder.FinishRequestException;
import name.martingeisse.grumpyrest.path.PathSegment;
import name.martingeisse.grumpyrest.path.PathUtil;
import name.martingeisse.grumpyrest.path.VariablePathSegment;
import name.martingeisse.grumpyrest.querystring.QuerystringParsingException;
import name.martingeisse.grumpyrest.responder.standard.StandardErrorResponder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.*;

public final class RequestCycle {

    private final RestApi api;
    private final HttpServletRequest servletRequest;
    private final HttpServletResponse servletResponse;
    private final ImmutableList<String> pathSegments;
    private Route matchedRoute;
    private ImmutableList<PathArgument> pathArguments;
    private final ResponseTransmitter responseTransmitter;

    public RequestCycle(RestApi api, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        this.api = api;
        this.servletRequest = servletRequest;
        this.servletResponse = servletResponse;

        String pathText = servletRequest.getServletPath();
        if (pathText == null) {
            this.pathSegments = ImmutableList.of();
        } else {
            this.pathSegments = ImmutableList.copyOf(PathUtil.splitIntoSegments(pathText));
        }

        this.responseTransmitter = new ResponseTransmitter() {

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

        };
    }

    public RestApi getApi() {
        return api;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }

    public ImmutableList<String> getPathSegments() {
        return pathSegments;
    }

    public Route getMatchedRoute() {
        return matchedRoute;
    }

    public ResponseTransmitter getResponseTransmitter() {
        return responseTransmitter;
    }

    void setMatchedRoute(Route matchedRoute) {
        Objects.requireNonNull(matchedRoute, "matchedRoute");

        ImmutableList<PathSegment> matchedRouteSegments = matchedRoute.path().segments();
        if (matchedRouteSegments.size() != pathSegments.size()) {
            throw new IllegalArgumentException("matched route has different number of segments than the path of this request cycle");
        }

        List<PathArgument> newPathArguments = new ArrayList<>();
        for (int i = 0; i < pathSegments.size(); i++) {
            PathSegment routeSegment = matchedRouteSegments.get(i);
            if (routeSegment instanceof VariablePathSegment variable) {
                newPathArguments.add(new PathArgument(this, variable.getVariableName(), pathSegments.get(i)));
            }
        }

        this.matchedRoute = matchedRoute;
        this.pathArguments = ImmutableList.copyOf(newPathArguments);
    }

    public <T> T parseBody(Class<T> clazz) {
        try {
            return api.getJsonEngine().parse(prepareParse(), clazz);
        } catch (JsonValidationException e) {
            throw new FinishRequestException(StandardErrorResponder.requestBodyValidationFailed(e));
        }
    }

    public <T> T parseBody(TypeToken<T> typeToken) {
        try {
            return api.getJsonEngine().parse(prepareParse(), typeToken);
        } catch (JsonValidationException e) {
            throw new FinishRequestException(StandardErrorResponder.requestBodyValidationFailed(e));
        }
    }

    public Object parseBody(Type type) {
        try {
            return api.getJsonEngine().parse(prepareParse(), type);
        } catch (JsonValidationException e) {
            throw new FinishRequestException(StandardErrorResponder.requestBodyValidationFailed(e));
        }
    }

    private InputStream prepareParse() {
        String contentType = servletRequest.getContentType();
        if (contentType == null || !contentType.equals("application/json")) {
            throw new FinishRequestException(StandardErrorResponder.JSON_EXPECTED);
        }
        try {
            return servletRequest.getInputStream();
        } catch (IOException e) {
            throw new FinishRequestException(StandardErrorResponder.IO_ERROR);
        }
    }

    public ImmutableList<PathArgument> getPathArguments() {
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
                throw new QuerystringParsingException(ImmutableMap.of("(root)", "querystring parser returned null"));
            }
        } catch (QuerystringParsingException e) {
            originalException = e;
            // duplicate-parameter errors take precedence here
            for (Map.Entry<String, String> entry : e.getFieldErrors().entrySet()) {
                errorMap.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }
        if (!errorMap.isEmpty()) {
            throw new QuerystringParsingException(ImmutableMap.copyOf(errorMap));
        }
        if (result == null) {
            // this can only happen if the originalException did not contain any errors
            throw originalException;
        }
        return result;
    }

}
