package name.martingeisse.grumpyrest;

import com.google.common.collect.ImmutableList;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import name.martingeisse.grumpyjson.JsonValidationException;
import name.martingeisse.grumpyrest.finish.FinishRequestException;
import name.martingeisse.grumpyrest.path.PathUtil;
import name.martingeisse.grumpyrest.responder.standard.StandardErrorResponder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

public final class RequestCycle {

    private final RestApi api;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ImmutableList<String> pathSegments;

    public RequestCycle(RestApi api, HttpServletRequest request, HttpServletResponse response) {
        this.api = api;
        this.request = request;
        this.response = response;

        String pathText = request.getServletPath();
        if (pathText == null) {
            this.pathSegments = ImmutableList.of();
        } else {
            this.pathSegments = ImmutableList.copyOf(PathUtil.splitIntoSegments(pathText));
        }
    }

    public RestApi getApi() {
        return api;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public ImmutableList<String> getPathSegments() {
        return pathSegments;
    }

    public <T> T parseBody(Class<T> clazz) throws JsonValidationException {
        return api.getJsonEngine().parse(prepareParse(), clazz);
    }

    public <T> T parseBody(TypeToken<T> typeToken) throws JsonValidationException {
        return api.getJsonEngine().parse(prepareParse(), typeToken);
    }

    public Object parseBody(Type type) throws JsonValidationException {
        return api.getJsonEngine().parse(prepareParse(), type);
    }

    private InputStream prepareParse() {
        String contentType = request.getContentType();
        if (contentType == null || !contentType.equals("application/json")) {
            throw new FinishRequestException(StandardErrorResponder.JSON_EXPECTED);
        }
        try {
            return request.getInputStream();
        } catch (IOException e) {
            throw new FinishRequestException(StandardErrorResponder.IO_ERROR);
        }
    }

}
