package name.martingeisse.grumpyrest;

import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyrest.querystring.QuerystringParsingException;

import java.lang.reflect.Type;
import java.util.List;

public interface Request {

    List<PathArgument> getPathArguments();

    <T> T parseQuerystring(Class<T> clazz) throws QuerystringParsingException;
    <T> T parseQuerystring(TypeToken<T> typeToken) throws QuerystringParsingException;
    Object parseQuerystring(Type type) throws QuerystringParsingException;

    <T> T parseBody(Class<T> clazz);
    <T> T parseBody(TypeToken<T> typeToken);
    Object parseBody(Type type);

}
