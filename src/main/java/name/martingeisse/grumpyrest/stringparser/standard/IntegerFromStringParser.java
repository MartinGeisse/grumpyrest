package name.martingeisse.grumpyrest.stringparser.standard;

import name.martingeisse.grumpyrest.stringparser.FromStringParser;
import name.martingeisse.grumpyrest.stringparser.FromStringParserException;

import java.lang.reflect.Type;

public final class IntegerFromStringParser implements FromStringParser {

    @Override
    public boolean supportsType(Type type) {
        return type.equals(Integer.TYPE) || type.equals(Integer.class);
    }

    @Override
    public Object parseFromString(String s, Type type) throws FromStringParserException {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new FromStringParserException("expected integer");
        }
    }

}
