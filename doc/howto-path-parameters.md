
# Path Parameters

Mount a route with a path that contains parameters (starting with a ':' character), then access the arguments bound
to those parameters as a list from the request:

```
api.addRoute("/:name", request -> "Hello " + request.getPathArguments().get(0).getValue(String.class) + "!");
```

* The URL contains the segment `:name` which is a path parameter because it starts with ':'
* `request.getPathArguments()` return the list of path arguments bound in the current request
* `.get(0)` selects the first of those arguments. If the path contains fixed segments as well as parameters, then the
  index passed to `.get` only counts parameters -- the fixed segments do not appear in the list. You might also wonder
  why `.get` takes an index even though the parameter has a name in the path: Selecting by index is simpler to use and
  not any more fragile (since names would not be compile-time checked either). The name is currently not used anywhere,
  but when reading the code, quickly shows the meaning of the parameter.
* `.getValue(String.class)` means to "parse" the parameter as a string, i.e. just return it as-is. Alternatively, you
  could parse numbers or booleans here, or some custom type you have defined in your application. Path parameters,
  like querystring parameters, use the from-string parsing system which allows to register parsers for arbitrary
  application types. The parser can also define how to "parse" a missing parameter.
  * It is currently not possible to specify a default value by the _caller_, only in the parser. This might be
    implemented in a future version. You can, however, parse the value as something like `OptionalField<Integer>`
    which parses a missing field to an `OptionalField` which `isAbsent()`, and then handle the missing value in the
    calling code.
