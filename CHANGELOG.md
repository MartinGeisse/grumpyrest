
# Version WIP (will be 0.3)

* Moved all classes that are related to the request (as opposed to the whole API, the request cycle or the response)
  into a single package.
* added lots of Javadoc
* added methods to JsonEngine to convert from/to JsonElement
* hide internal exceptions that occur in type adapters from the client

* WIP: distinguish routes by HTTP method

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
