package name.martingeisse.grumpyrest;

import com.google.common.collect.ImmutableList;
import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyrest.querystring.QuerystringParsingException;

import java.lang.reflect.Type;

public interface Request {

    ImmutableList<PathArgument> getPathArguments();

    <T> T parseQuerystring(Class<T> clazz) throws QuerystringParsingException;
    <T> T parseQuerystring(TypeToken<T> typeToken) throws QuerystringParsingException;
    Object parseQuerystring(Type type) throws QuerystringParsingException;

    <T> T parseBody(Class<T> clazz);
    <T> T parseBody(TypeToken<T> typeToken);
    Object parseBody(Type type);

}
