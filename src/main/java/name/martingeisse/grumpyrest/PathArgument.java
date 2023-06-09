package name.martingeisse.grumpyrest;

import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyrest.stringparser.FromStringParserException;

import java.lang.reflect.Type;

public final class PathArgument {

    private final RequestCycle requestCycle;
    private final String name;
    private final String text;

    public PathArgument(RequestCycle requestCycle, String name, String text) {
        this.requestCycle = requestCycle;
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public <T> T getValue(Class<T> clazz) throws FromStringParserException {
        return clazz.cast(getValue((Type)clazz));
    }

    public <T> T getValue(TypeToken<T> typeToken) throws FromStringParserException {
        //noinspection unchecked
        return (T)getValue(typeToken.getType());
    }

    public Object getValue(Type type) throws FromStringParserException {
        return requestCycle.getApi().getFromStringParserRegistry().getParser(type).parseFromString(text, type);
    }

}
