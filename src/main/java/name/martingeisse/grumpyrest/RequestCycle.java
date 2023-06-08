package name.martingeisse.grumpyrest;

import com.google.common.collect.ImmutableList;
import name.martingeisse.grumpyrest.path.PathUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class RequestCycle {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ImmutableList<String> pathSegments;

    public RequestCycle(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;

        String pathText = request.getServletPath();
        if (pathText == null) {
            this.pathSegments = ImmutableList.of();
        } else {
            this.pathSegments = ImmutableList.copyOf(PathUtil.splitIntoSegments(pathText));
        }
        System.out.println(pathSegments);
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
