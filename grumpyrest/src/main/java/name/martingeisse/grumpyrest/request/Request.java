package name.martingeisse.grumpyrest.request;

import com.google.gson.reflect.TypeToken;
import name.martingeisse.grumpyjson.JsonEngine;
import name.martingeisse.grumpyjson.JsonRegistry;
import name.martingeisse.grumpyrest.RequestCycle;
import name.martingeisse.grumpyrest.SimpleHandler;
import name.martingeisse.grumpyrest.request.querystring.QuerystringParserRegistry;
import name.martingeisse.grumpyrest.request.querystring.QuerystringParsingException;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParserRegistry;

import java.lang.reflect.Type;
import java.util.List;

/**
 * This interface provides access to all properties of an HTTP request that are relevant for the REST API. It serves
 * two purposes:
 * <ul>
 *     <li>it allows {@link SimpleHandler} to depend only on {@link Request}, decoupling it from {@link RequestCycle},
 *       and therefore simplifies testing and mocking</li>
 *     <li>it removes the need for getter methods for request properties in {@link RequestCycle} and so helps to
 *       keep it more tidy</li>
 * </ul>
 * <p>
 * This interface currently provides acces to path arguments, querystring arguments and the request body. In the
 * future, it will be extended to access request headers.
 */
public interface Request {

    /**
     * Getter method for the path arguments bound to path parameters in the matched route.
     * <p>
     * The returned list contains one element per path parameter. That is, literal path segments do not appear in
     * the returned list.
     * <p>
     * The path arguments keep the value of the corresponding segments of the request path in their textual form. No
     * parsing or mapping to application types has been performed at this point. Instead, parsing as an application
     * type is done by calling methods on the returned {@link PathArgument} objects.
     * <p>
     * This method may only be called after a route has been matched and the match result applied to the request --
     * binding path arguments -- but once a handler gets called, this is the case.
     *
     * @return the path arguments
     */
    List<PathArgument> getPathArguments();

    /**
     * Parses the whole querystring into an object. This object is usually a Java record with a one-to-one mapping
     * of querystring parameters to record fields, but custom parsing can be defined through the
     * {@link QuerystringParserRegistry}. Parsers for yet unknown record types will be auto-generated, and parsers
     * for the individual fields will be taken from the {@link FromStringParserRegistry}.
     *
     * @param clazz the class to parse as, usually a record class
     * @return the parsed object
     * @param <T> the static type of the class to parse as
     * @throws QuerystringParsingException  on parsing errors, such as wrongly formatted fields, unknown fields,
     * missing fields or duplicate fields
     */
    <T> T parseQuerystring(Class<T> clazz) throws QuerystringParsingException;

    /**
     * Parses the whole querystring into an object. This object is usually a Java record with a one-to-one mapping
     * of querystring parameters to record fields, but custom parsing can be defined through the
     * {@link QuerystringParserRegistry}. Parsers for yet unknown record types will be auto-generated, and parsers
     * for the individual fields will be taken from the {@link FromStringParserRegistry}.
     *
     * @param typeToken a type token for the type to parse as
     * @return the parsed object
     * @param <T> the static type to parse as
     * @throws QuerystringParsingException on parsing errors, such as wrongly formatted fields, unknown fields,
     * missing fields or duplicate fields
     */
    <T> T parseQuerystring(TypeToken<T> typeToken) throws QuerystringParsingException;

    /**
     * Parses the whole querystring into an object. This object is usually a Java record with a one-to-one mapping
     * of querystring parameters to record fields, but custom parsing can be defined through the
     * {@link QuerystringParserRegistry}. Parsers for yet unknown record types will be auto-generated, and parsers
     * for the individual fields will be taken from the {@link FromStringParserRegistry}.
     *
     * @param type the type to parse as
     * @return the parsed object
     * @throws QuerystringParsingException on parsing errors, such as wrongly formatted fields, unknown fields,
     * missing fields or duplicate fields
     */
    Object parseQuerystring(Type type) throws QuerystringParsingException;

    /**
     * Parses the request body using the JSON parsing mechanism defined by {@link JsonEngine} and the JSON-able types
     * defined in the engine's {@link JsonRegistry}. Refer to these classes for details.
     *
     * @param clazz the class to parse as
     * @return the parsed object
     * @param <T> the static type of the class to parse as
     */
    <T> T parseBody(Class<T> clazz);

    /**
     * Parses the request body using the JSON parsing mechanism defined by {@link JsonEngine} and the JSON-able types
     * defined in the engine's {@link JsonRegistry}. Refer to these classes for details.
     *
     * @param typeToken a type token for the type to parse as
     * @return the parsed object
     * @param <T> the static type to parse as
     */
    <T> T parseBody(TypeToken<T> typeToken);

    /**
     * Parses the request body using the JSON parsing mechanism defined by {@link JsonEngine} and the JSON-able types
     * defined in the engine's {@link JsonRegistry}. Refer to these classes for details.
     *
     * @param type the type to parse as
     * @return the parsed object
     */
    Object parseBody(Type type);

}
