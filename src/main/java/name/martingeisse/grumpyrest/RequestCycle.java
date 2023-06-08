package name.martingeisse.grumpyrest;

import com.google.common.collect.ImmutableList;
import name.martingeisse.grumpyrest.path.PathUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

}
