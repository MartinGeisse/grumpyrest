/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package io.github.grumpystuff.grumpyrest_demo.shop;

import io.github.grumpystuff.grumpyrest.response.FinishRequestException;
import io.github.grumpystuff.grumpyrest.response.standard.StandardErrorResponse;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * Simulates a database table. Note that unlike a relational database, the ID is not part of the row data but stored
 * next to the row -- it is the index in the internal list.
 */
public final class Table<T> {

    private final List<T> rows = new ArrayList<>();

    public synchronized int insert(T row) {
        Objects.requireNonNull(row, "row");
        int id = rows.size();
        rows.add(row);
        return id;
    }

    private boolean isIdInRange(int id) {
        return (id >= 0 && id < rows.size());
    }

    public synchronized boolean isValidId(int id) {
        return isIdInRange(id) && rows.get(id) != null;
    }

    public synchronized T get(int id) {
        T result = getOrNull(id);
        if (result == null) {
            throw new IllegalArgumentException("invalid id: " + id);
        }
        return result;
    }

    public synchronized T getRestEquivalent(int id) {
        T result = getOrNull(id);
        if (result == null) {
            throw new FinishRequestException(StandardErrorResponse.ID_NOT_FOUND);
        }
        return result;
    }

    public synchronized T getOrNull(int id) {
        return isIdInRange(id) ? rows.get(id) : null;
    }

    public synchronized boolean exists(int id) {
        return getOrNull(id) != null;
    }

    public synchronized boolean existsAny(Predicate<T> filter) {
        for (T row : rows) {
            if (row != null && filter.test(row)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void replace(int id, T row) {
        Objects.requireNonNull(row, "row");
        if (!isValidId(id)) {
            throw new IllegalArgumentException("invalid id: " + id);
        }
        rows.set(id, row);
    }

    public synchronized void delete(int id) {
        if (!isValidId(id)) {
            throw new IllegalArgumentException("invalid id: " + id);
        }
        rows.set(id, null);
    }

    public synchronized void deleteIf(Predicate<T> filter) {
        for (int i = 0; i < rows.size(); i++) {
            T row = rows.get(i);
            if (row != null && filter.test(row)) {
                rows.set(i, null);
            }
        }
    }

    /**
     * Returns the first element that matches the filter, or null if none.
     */
    public synchronized Pair<Integer, T> getFirst(Predicate<T> filter) {
        for (int i = 0; i < rows.size(); i++) {
            T row = rows.get(i);
            if (row != null && filter.test(row)) {
                return Pair.of(i, row);
            }
        }
        return null;
    }

    /**
     * Filters the elements of this table and maps them to a different type. Return null from the body to filter out
     * an element.
     */
    public synchronized <U> List<U> filterMap(BiFunction<Integer, T, U> body) {
        List<U> result = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            T row = rows.get(i);
            if (row != null) {
                U resultElement = body.apply(i, row);
                if (resultElement != null) {
                    result.add(resultElement);
                }
            }
        }
        return List.copyOf(result);
    }

    public synchronized void foreach(BiConsumer<Integer, T> consumer) {
        for (int i = 0; i < rows.size(); i++) {
            T row = rows.get(i);
            if (row != null) {
                consumer.accept(i, row);
            }
        }
    }

}
