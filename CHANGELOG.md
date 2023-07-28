
## Version 0.6 (WIP)

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

...

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
