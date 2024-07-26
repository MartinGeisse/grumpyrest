
## Version 0.9

* Removed the grumpyrest-jetty-launcher subproject. This project contained only two helper classes that at best helped
  start a project with Grumpyrest, but had to be replaced later anyway to allow changes. Such code is better located
  in a demo, so I moved these two classes into the grumpyrest-demo subproject.

## Version 0.8

* Make the built-in converters use the `JsonProviders` interface to fetch the converters they depend on, instead of
  depending on the full `JsonRegistries` type.
* Work on decoupling from Gson as the only possible JSON library:
  * Added our own `TypeToken` class to use instead of Gson's one
  * Added our own JSON model classes (`JsonBoolean`, `JsonNumber` etc.) to use instead of Gson's ones
  * Split off `StructuralJsonEngine` as a base class of `JsonEngine`. This class provides conversion between domain
    types and the JSON model classes only, without support for the actual JSON _syntax_. The latter gets added by
    `JsonEngine`.
  * Split off `GsonBasedJsonEngine` as a subclass of `JsonEngine` to provide JSON syntax support using Gson. It converts
    between Gson's JSON model classes and our own to do that. `JsonEngine` is now `abstract` and defines the methods
    for handling the JSON syntax, but does not implement them to avoid a dependency on any concrete JSON parser /
    formatter such as Gson.
  * The `RestApi` constructor expects the `JsonEngine` as an argument now to make it independent of the concrete JSON
    syntax implementation.
* Grumpyrest is now getting published on Maven Central
* Replaced the Gradle-based build system by a Maven-based one
* added lots of checks against null arguments
* minor bugfixes, improved error messages etc.

## Version 0.7

* reduced minimum Java version from 20 to 17
* `NullableField` and `OptionalField`now have `.orElse()`and `.orElseGet()` methods to easily provide default values.
* Add argument checks against null to many methods
* improve the checks against loss of precision or range when deserializing integral types
* add toString() support to `Path` and `PathSegment`

## Version 0.6

* The order in which the lists of manually added JSON converters, parameter parsers etc. are scanned is now REVERSED!
  * converters/parsers added later now take precedence over ones added earlier. For a given type, of all converters
    that could handle that type, the one added last will be selected. 
  * this allows to provide standard converters in the framework and the application code can override them just by
    adding its own converters.
  * Previously, application code had to clear the registries, then re-add all standard converters it wanted to keep.
    To override a single type (for example, change the `LocalDate` converter to be more lenient and make leading
    zeroes optional), the list of all other standard converters would have to be cloned from the framework into
    application code and have to be updated every time the framework adds new standard converters.
* added LocalDate / LocalDateTime / LocalTime JSON converters and parsers
* added enum converters and parsers
* added option to record converter to ignore unknown properties

## Version 0.5

* More javadoc
* split type adapters in serializers and deserializers
  * deserializers will always work on the static type (because there is no run-time class yet)
  * serializers will always work on the run-time class, never on the static type (because the alternative would be
    a weird mix of both that is ridiculously hard to understand and doesn't provide any advantage)
* Some internal decoupling / refactoring
  * added Registry base class
  * (De)SerializerProvider: Interface to use in other type adapters so they don't have to depend on the whole registry
* Maps / objects with arbitrary keys
* HTTP headers (request and response)

# Version 0.4

* added TypeWrapper to serialize top-level lists (but the next version will probably solve this differently)
* removed Apache Commons dependency
* even more documentation and Javadoc

# Version 0.3

* Moved all classes that are related to the request (as opposed to the whole API, the request cycle or the response)
  into a single package.
* added lots of documentation, including Javadoc
* added methods to JsonEngine to convert from/to JsonElement
* hide internal exceptions that occur in type adapters from the client
* multiple routes can now use the same path and be distinguished by HTTP method
* added a helper package to launch a minimal Jetty with a REST API

# Version 0.2

* Split into sub-packages: grumpyjson, grumpyrest, grumptyrest-demo.
* Removed unnecessary dependencies; now uses JDK immutable collections such as List.copyOf() instead of Guava.
* added maven-publish plugin to allow publishing to the local maven Repo at `~/.m2` including sources and javadoc
* clean up the documentation
* added lots of javadoc, more to come
* combined javadoc is now available online
* started abstracting the objects provided by grumpyrest  from the servlet API to simplify mocking them in tests
* added option to include/exclude the context path and servlet path in the request path as seen by the API
* uncaught exceptions are now logged

# Version 0.1

* Initial release. Also published on Hacker News.
