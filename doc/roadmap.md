
# Roadmap

## Near Future

* seal routes, responseFactoryRegistry in RestApi
* add actual tests to JsonEngineTest
* More JSON types
  * record with extra keys as Map<>
  * discriminated union
  * Instant (seconds / milli)

## Medium-Term

* provide a tutorial (I'll delay this until the API has stabilized)
* HOWTO: add response JSON types (existing HOWTO covers request body deserialization)

## Features I might add if there is demand and they can be defined well enough

* `ZonedDateTime` converter. There is no standardized format for a `ZonedDateTime`, and Java just made up its own
  format. That format also redundantly specifies the region _and_ offset. So to support this, first a format has to
  be agreed on.
* Multiple `JsonRegistries` / `FromStringParserRegistry` objects for different routes. I can see how this is useful
  if you serve multiple completely different APIs with different JSON mappings under different paths, or maybe use
  paths for API versioning and want to change the format in a newer version. OTOH it is already possible to just build
  multiple `RestApi` objects and route a request to one of them at the servlet level, so maybe the added value of such
  a feature isn't that great.
* Request filters / interceptors. Many if the features these would allow, such as authentication/authorization, are
  already possible by wrapping the handlers of your routes with a handler-wrapper that does the job of an interceptor.
  Wrapping has the advantage that you can change the handler interface and easily pass additional information to the
  wrapped handler. Features which are _not_ easily possible by wrapping the handler include:
  * adding information that can be used for route matching. Alternatives are custom matching code in the routes, or
    setting a central matching strategy. "Adding information" is currently not possible -- we would have to define
    how that information gets stored first.
  * changing the path before route matching ("internal redirects"), though this can currently be solved on the
    servlet level
  * you can forget to wrap a handler, though this is only the case if the wrapped handler uses the exact same
    `SimpleHandler` or `ComplexHandler` interface.
* custom enum matchers, to allow case-insensitive matching / lowercase matching / make an enum constant like
  `FOO_BAR` publicly look like `foo-bar`
* add a JSON converter that allows arbitrary extra fields in a record and collects them into a `Map` which is a
  field in that record.

