
# Roadmap

## Version 0.5 or 0.6

* serialize raw lists. this will probably trigger a huge redesign WRT type adapters: 
  * split type adapters in serializers and deserializers
  * deserializers will always work on the static type (because there is no run-time class yet)
  * serializers will always work on the run-time class, never on the static type (because the alternative would be
    a weird mix of both that is ridiculously hard to understand and doesn't provide any advantage)
* Some internal decoupling / refactoring
  * TypeAdapterProvider: Interface to use in other type adapters so they don't have to depend on the whole registry
    * with the above split, there would actually be a SerializerProvider and a DeserializerProvider
* Possibly: Request filters / interceptors. May instead result in documentation on how to achieve this with the current code.
* Maps / objects with arbitrary keys
* objects that allow arbitrary extra keys

## Future

* provide a tutorial (I'll delay this until the API has stabilized)
