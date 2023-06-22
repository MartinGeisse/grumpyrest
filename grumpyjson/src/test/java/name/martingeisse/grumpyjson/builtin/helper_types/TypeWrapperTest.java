/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyjson.builtin.helper_types;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TypeWrapperTest {

    @Test
    public void test() {
        TypeWrapper<Integer> typeWrapper = new TypeWrapper<Integer>(123) {};
        Assertions.assertEquals(123, typeWrapper.getValue());
        Assertions.assertEquals(Integer.class, typeWrapper.getType());
    }

}
