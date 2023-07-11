/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.request.querystring;

import name.martingeisse.grumpyjson.builtin.record.RecordInfo;
import name.martingeisse.grumpyrest.ExceptionMessages;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParser;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParserException;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParserRegistry;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * This class implements an auto-generated record parser.
 */
public final class QuerystringToRecordParser implements QuerystringParser {

    private final RecordInfo recordInfo;
    private final FromStringParserRegistry fromStringParserRegistry;

    QuerystringToRecordParser(Class<?> rawRecordClass, FromStringParserRegistry fromStringParserRegistry) {
        Objects.requireNonNull(rawRecordClass, "rawRecordClass");
        Objects.requireNonNull(fromStringParserRegistry, "fromStringParserRegistry");
        this.recordInfo = new RecordInfo(rawRecordClass);
        this.fromStringParserRegistry = fromStringParserRegistry;
    }

    @Override
    public boolean supportsType(Type type) {
        Objects.requireNonNull(type, "type");
        if (type instanceof Class<?>) {
            return type.equals(recordInfo.getRecordClass());
        } else if (type instanceof ParameterizedType p && p.getRawType() instanceof Class<?>) {
            return p.getRawType().equals(recordInfo.getRecordClass());
        } else {
            return false;
        }
    }

    @Override
    public Object parse(Map<String, String> querystring, Type recordType) throws QuerystringParsingException {
        Objects.requireNonNull(querystring, "querystring");
        Objects.requireNonNull(recordType, "recordType");

        List<RecordInfo.ComponentInfo> componentInfos = recordInfo.getComponentInfos();
        int numberOfPresentParameters = 0;
        Object[] fieldValues = new Object[componentInfos.size()];
        Map<String, String> fieldErrors = new HashMap<>();

        for (int i = 0; i < componentInfos.size(); i++) {
            RecordInfo.ComponentInfo componentInfo = componentInfos.get(i);
            String name = componentInfo.getName();
            String value = querystring.get(name);
            if (value != null) {
                numberOfPresentParameters++;
            }
            try {
                Type concreteFieldType = componentInfo.getConcreteType(recordType);
                FromStringParser parser = fromStringParserRegistry.get(concreteFieldType);
                if (value == null) {
                    fieldValues[i] = parser.parseFromAbsentString(concreteFieldType);
                } else {
                    fieldValues[i] = parser.parseFromString(value, concreteFieldType);
                }
            } catch (FromStringParserException e) {
                fieldErrors.put(name, e.getMessage());
            } catch (Exception e) {
                fieldErrors.put(name, "parse error");
            }
        }

        if (numberOfPresentParameters != querystring.size()) {
            // this is more expensive, so only do this if there is really an error
            Set<String> propertyNames = new HashSet<>(querystring.keySet());
            for (RecordInfo.ComponentInfo componentInfo : componentInfos) {
                propertyNames.remove(componentInfo.getName());
            }
            for (String unexpectedProperty : propertyNames) {
                fieldErrors.put(unexpectedProperty, ExceptionMessages.UNEXPECTED_PARAMETER);
            }
        }

        if (!fieldErrors.isEmpty()) {
            throw new QuerystringParsingException(Map.copyOf(fieldErrors));
        }
        return recordInfo.invokeConstructor(fieldValues);
    }

}
