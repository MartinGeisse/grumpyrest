package name.martingeisse.grumpyrest.request;

import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyrest.RequestCycle;
import name.martingeisse.grumpyrest.SimpleHandler;
import name.martingeisse.grumpyrest.request.querystring.QuerystringParsingException;

import java.lang.reflect.Type;
import java.util.List;

/**
 * This interface provides access to all properties of an HTTP request that are relevant for the REST API. It serves
 * two purposes:
 * <ul>
 *     <li>it allows {@link SimpleHandler} to depend only on {@link Request}, decoupling it from {@link RequestCycle},
 *       and therefore simplifies testing and mocking</li>
 *     <li>it removes the need for getter methods for request properties in {@link RequestCycle} and so helps to
 *       keep it more tidy</li>
 * </ul>
 * This interface currently provides acces to path arguments, querystring arguments and the request body. In the
 * future, it will be extended to access request headers.
 */
public interface Request {

    List<PathArgument> getPathArguments();

    <T> T parseQuerystring(Class<T> clazz) throws QuerystringParsingException;
    <T> T parseQuerystring(TypeToken<T> typeToken) throws QuerystringParsingException;
    Object parseQuerystring(Type type) throws QuerystringParsingException;

    <T> T parseBody(Class<T> clazz);
    <T> T parseBody(TypeToken<T> typeToken);
    Object parseBody(Type type);

}
