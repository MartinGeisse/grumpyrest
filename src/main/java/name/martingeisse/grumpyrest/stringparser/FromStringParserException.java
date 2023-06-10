/*
 * Copyright (c) 2023 Martin Geisse
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package name.martingeisse.grumpyrest.stringparser;

public class FromStringParserException extends Exception {

    public enum FieldKind {
        PATH_PARAMETER,
        QUERYSTRING_PARAMETER,
    }

    private FieldKind fieldKind;
    private String fieldName;

    public FromStringParserException(String message) {
        super(message);
    }

    public void setLocation(FieldKind fieldKind, String fieldName) {
        this.fieldKind = fieldKind;
        this.fieldName = fieldName;
    }

    public final FieldKind getFieldKind() {
        return fieldKind;
    }

    public final String getFieldName() {
        return fieldName;
    }

}
