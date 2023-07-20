
# Roadmap

## Future

* specify path/querystring parameter defaults by the caller, not the parser
  * check if this is even necessary. Querystring parsing can use OptionalField and path parameters can't be optional
    because then the route would not match
* provide a tutorial (I'll delay this until the API has stabilized)
* seal routes, responseFactoryRegistry in RestApi
* Possibly: Request filters / interceptors. May instead result in documentation on how to achieve this with the current code.
* objects that allow arbitrary extra keys
* HOWTO: add response JSON types (existing HOWTO covers request body deserialization)
* add actual tests to JsonEngineTest



