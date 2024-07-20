package name.martingeisse.grumpyrest.request;

import name.martingeisse.grumpyjson.JsonEngine;
import name.martingeisse.grumpyjson.JsonRegistries;
import name.martingeisse.grumpyjson.TypeToken;
import name.martingeisse.grumpyrest.ComplexHandler;
import name.martingeisse.grumpyrest.RequestCycle;
import name.martingeisse.grumpyrest.SimpleHandler;
import name.martingeisse.grumpyrest.request.querystring.QuerystringParserRegistry;
import name.martingeisse.grumpyrest.request.querystring.QuerystringParsingException;
import name.martingeisse.grumpyrest.request.stringparser.FromStringParserRegistry;

import java.lang.reflect.Type;
import java.util.List;

/**
 * This interface provides access to all properties of an HTTP request that are relevant for the REST API.
 * <p>
 * A {@link SimpleHandler} only gets this interface and returns a response value from that. For most bases, this is
 * sufficient. Complex cases implement {@link ComplexHandler} instead to get access to the whole {@link RequestCycle}.
 * However, even then, the handler accesses request properties using this interface, by calling
 * {@link RequestCycle#getHighlevelRequest()}.
 */
public interface Request {

    /**
     * Getter method for the HTTP method.
     *
     * @return the HTTP method
     */
    String getMethod();

    /**
     * Getter method for HTTP request headers. Returns null if a header is not present.
     * <p>
     * Neither this method nor its return value provides any high-level parsing. There is simply not enough
     * consistency between the various HTTP headers to make this useful.
     *
     * @param name the name of the header to return
     * @return the header value, as a string, or null if not present
     */
    String getHeader(String name);

    /**
     * Getter method for the path arguments bound to path parameters in the matched route.
     * <p>
     * The returned list contains one element per path parameter. That is, literal path segments do not appear in
     * the returned list.
     * <p>
     * The path arguments keep the values of the corresponding segments of the request path in their textual form. No
     * parsing or mapping to application types has been performed at this point. Instead, parsing as an application
     * type is done by calling methods on the returned {@link PathArgument} objects.
     * <p>
     * Both {@link SimpleHandler} and {@link ComplexHandler} can always call this method. During request processing,
     * and <i>before</i> the handler gets called, however, there is a point where this method is not allowed to be
     * called. This is before a route has been matched and the match result applied to the request.
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
     * defined in the engine's {@link JsonRegistries}. Refer to these classes for details.
     *
     * @param clazz the class to parse as
     * @return the parsed object
     * @param <T> the static type of the class to parse as
     */
    <T> T parseBody(Class<T> clazz);

    /**
     * Parses the request body using the JSON parsing mechanism defined by {@link JsonEngine} and the JSON-able types
     * defined in the engine's {@link JsonRegistries}. Refer to these classes for details.
     *
     * @param typeToken a type token for the type to parse as
     * @return the parsed object
     * @param <T> the static type to parse as
     */
    <T> T parseBody(TypeToken<T> typeToken);

    /**
     * Parses the request body using the JSON parsing mechanism defined by {@link JsonEngine} and the JSON-able types
     * defined in the engine's {@link JsonRegistries}. Refer to these classes for details.
     *
     * @param type the type to parse as
     * @return the parsed object
     */
    Object parseBody(Type type);

}
