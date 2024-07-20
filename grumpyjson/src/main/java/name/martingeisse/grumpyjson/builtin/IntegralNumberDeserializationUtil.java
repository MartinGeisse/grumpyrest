/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin;

import name.martingeisse.grumpyjson.deserialize.JsonDeserializationException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * NOT PUBLIC API
 */
/*
This class solves the problem that it is surprisingly tricky in Java to either convert a Number object to a specific
integral type (such as int or long), or fail if the number is not representable in that type. It is tricky because
any problem representing the number in the target type also makes it hard to check the result for correctness. Java
does not provide an equals() method for Number either.

Other libraries solve the problem by enumerating different Number implementations, calling .doubleValue(), or calling
.toString() and parsing the result as a double. We do the same except toString/parsing.

The problem does not occur for non-integral target types. For those, a loss of precision is acceptable and sometimes
even desired. For example, users would be confused if the number 0.1 in JSON was rejected for target type double
because that value cannot be represented exactly. Also, all of Java's non-integral types are unbounded.
 */
final class IntegralNumberDeserializationUtil {

    // prevent instantiation
    private IntegralNumberDeserializationUtil() {
    }

    // ----------------------------------------------------------------------------------------------------------------
    // integral
    // ----------------------------------------------------------------------------------------------------------------

    static long deserialize(Number number) throws JsonDeserializationException {
        Objects.requireNonNull(number, "number");

        if (number instanceof Long l) {
            return l;
        } else if (number instanceof Integer i) {
            return i;
        } else if (number instanceof Short s) {
            return s;
        } else if (number instanceof Byte b) {
            return b;
        } else if (number instanceof Double d) {
            return deserializeFromDouble(d);
        } else if (number instanceof Float f) {
            return deserializeFromDouble(f);
        } else if (number instanceof BigInteger bi) {
            try {
                return bi.longValueExact();
            } catch (ArithmeticException _ignored) {
                throw new JsonDeserializationException("value out of bounds: " + bi);
            }
        } else if (number instanceof BigDecimal bd) {
            try {
                return bd.longValueExact();
            } catch (ArithmeticException _ignored) {
                throw new JsonDeserializationException("value out of bounds or has unexpected fractional digits: " + bd);
            }
        } else {
            return number.longValue();
        }
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    private static long deserializeFromDouble(double original) throws JsonDeserializationException {
        // Conversion of long back to double will never overflow but might lose precision. The only "really bad"
        // scenario we have to consider is that conversion to long wraps around, producing least-significant
        // digits, and then the conversion back loses them. In that case, the magitude will differ a lot, so
        // converting back and comparing with the original will catch all conversion errors.
        long converted = (long)original;
        double convertedBack = converted;
        if (convertedBack != original) {
            throw new JsonDeserializationException("value out of bounds or has unexpected fractional digits: " + original);
        }
        return converted;
    }

    static void verifyBounds(long original, long converted) throws JsonDeserializationException {
        if (converted != original) {
            throw new JsonDeserializationException("value out of bounds: " + original);
        }
    }

}
