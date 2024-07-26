/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyjson.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ListUtilTest {

    @Test
    public void testEmpty() {
        List<Integer> list = new ArrayList<>();
        ListUtil.reverseInPlace(list);
        Assertions.assertEquals(List.of(), list);
    }

    @Test
    public void testOne() {
        List<Integer> list = new ArrayList<>(List.of(1));
        ListUtil.reverseInPlace(list);
        Assertions.assertEquals(List.of(1), list);
    }

    @Test
    public void testThree() {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
        ListUtil.reverseInPlace(list);
        Assertions.assertEquals(List.of(3, 2, 1), list);
    }

}
