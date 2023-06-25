
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
