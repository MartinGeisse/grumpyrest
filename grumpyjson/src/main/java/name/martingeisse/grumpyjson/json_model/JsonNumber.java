package name.martingeisse.grumpyjson.json_model;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This class represents JSON numbers.
 */
public final class JsonNumber extends JsonElement {

    /**
     * Creates an instance of this class for the specified plain numeric value.
     * <p>
     * To guarantee immutability of the whole JSON structure, the argument must be an immutable {@link Number}
     * instance itself.
     *
     * @param value the numeric value -- must be immutable
     * @return the JSON number
     */
    public static JsonNumber of(Number value) {
        return new JsonNumber(value);
    }

    private final Number value;

    private JsonNumber(Number value) {
        this.value = value;
    }

    /**
     * Getter method.
     *
     * @return the value of this JSON number
     */
    public Number getValue() {
        return value;
    }

    @Override
    public Number deserializerExpectsNumber() {
        return value;
    }

    @Override
    public boolean equals(Object otherObject) {
        if (!(otherObject instanceof JsonNumber otherJsonNumber)) {
            return false;
        }
        Number otherNumber = otherJsonNumber.value;

        // for two numbers to be equal, at least these equalities must hold
        if (value.longValue() != otherNumber.longValue() || value.doubleValue() != otherNumber.doubleValue()) {
            return false;
        }

        // check in a more detailed way if any of the two numbers is a BigDecimal or BigInteger
        if (value instanceof BigDecimal d) {
            return detailedEquals(d, otherNumber);
        } else if (otherNumber instanceof BigDecimal d) {
            return detailedEquals(d, value);
        } else if (value instanceof BigInteger i) {
            return detailedEquals(new BigDecimal(i), otherNumber);
        } else if (otherNumber instanceof BigInteger i) {
            return detailedEquals(new BigDecimal(i), value);
        } // else: either standard numeric types or exotic types... in either case, the above long/double equality check should be enough

        return true;
    }

    private static boolean detailedEquals(BigDecimal x, Number y) {
        if (y instanceof BigDecimal yd) {
            return x.compareTo(yd) == 0;
        } else if (y instanceof BigInteger yi) {
            return x.compareTo(new BigDecimal(yi)) == 0;
        } else if (y instanceof Long yi) {
            return x.compareTo(BigDecimal.valueOf(yi)) == 0;
        } else {
            return x.compareTo(BigDecimal.valueOf(y.doubleValue())) == 0;
        }
    }

    @Override
    public int hashCode() {
        // different types might produce different hash codes for the same numeric value, so make sure we use the same type
        return Double.hashCode(value.doubleValue());
    }

    @Override
    public String toString() {
        return "JSON:" + value;
    }

}
