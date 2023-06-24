
# Roadmap

## Version 0.5

* More documentation
  * especially JSON-related topics
  * also: provide tutorials, howtos, tech reference (javadoc), explanation (architecture and concept)
* serialize raw lists? May have to wait for serializer/deserializer split in 0.6
* further reduce external dependencies

## Version 0.6

* Possibly: split type adapters in serializers and deserializers
* Some internal decoupling / refactoring
  * TypeAdapterProvider: Interface to use in other type adapters so they don't have to depend on the whole registry
* Possibly: Request filters / interceptors. May instead result in documentation on how to achieve this with the current code.

## Future

specify path/querystring parameter defaults by the caller, not the parser

should optimize non-generic records, as well as fields whose types do not use type variables (performance)
