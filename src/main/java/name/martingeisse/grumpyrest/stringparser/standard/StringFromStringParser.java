package name.martingeisse.grumpyrest.stringparser.standard;

import name.martingeisse.grumpyrest.stringparser.FromStringParser;

import java.lang.reflect.Type;

public final class StringFromStringParser implements FromStringParser {

    @Override
    public boolean supportsType(Type type) {
        return type.equals(String.class);
    }

    @Override
    public Object parseFromString(String s, Type type) {
        return s;
    }

}
