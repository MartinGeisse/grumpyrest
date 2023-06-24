
# Request Body

A simple example is in the README already:

```
record MakeGreetingRequest(String name, OptionalField<String> addendum) {}
...
MakeGreetingRequest requestBody = request.parseBody(MakeGreetingRequest.class);
```

Parsing will fail with a `JsonValidationException` if anything is wrong. Compared to other frameworks, grumpyrest is
very strict and will fail validation when:
* a JSON object contains unknown fields (they will not be ignored)
* a number was given where a string was expected (there is no coercion)
* a number with a fraction was given where an integer was expected (there is no implicit rounding or truncation)
* a field is missing in the JSON (this won't result in a null reference in Java)
  * even if the Java code uses the class `NullableField`, because nullable is not the same as optional
  * only `OptionalField` allows the field to be absent
* a field is null in the JSON (this won't result in a null reference in Java)
  * even if the Java code uses the class `OptionalField`, because optional is not the same as nullable
  * `NullableField` allows null values. Other types may define an explicit meaning for null values and accept
    them too, but none of this happens implicitly.

You can use `OptionalField<NullableField<...>>` to define a field that can be missing, null, or something. Watch out
for the nesting order! It will only work this way, not with `NullableField<OptionalField<...>>` (we might want to
review this, maybe it can be changed).

Parameterized types such as `OptionalField`, `NullableField` and `List` must use concrete type arguments. A raw
`List`, `List<?>` or something like that cannot be parsed because the parser then lacks the information about the
type of the elements -- the JSON itself won't tell the parser which Java class to parse as.

Speaking of lists, a subtype like `ArrayList` won't work either because there is no general-purpose way to create
instances of such classes -- a list subclass may expect a no-arg constructor followed by setter calls, or a constructor
that takes a list of elements, or a constructor that takes an array of elements, or a static factory method with any of
these styles. Just use `List<SomeConcreteType>` and you will get exactly that.

## TODO: Maps / objects with arbitrary keys (not yet supported)

## TODO: objects that allow arbitrary extra keys (not yet supported)

## Custom Types

You can extend grumpyrest with custom types, both for request body parsing and response body generation. Both cases
are implemented as a class that implements `JsonTypeAdapter` and gets registered in the `JsonRegistry` of the
`JsonEngine` like this:

```
restApi.getJsonEngine().addTypeAdapter(new MyTypeAdapter());
```

This interface must provide three methods:
* `boolean supportsType(Type type)`: to ask the type adapter which types it supports. This cannot currently distinguish
  between parsing and serialization, so if you want to support both for a specific type, then they must be implemented
  by the same type adapter. This method can be as easy as `type.equals(MyCustomType.class)` if that type isn't an
  interface, is not part of a class hierarchy and is not generic. For more complex cases, refer to the JDK documentation
  about `Type`.
* `T fromJson(JsonElement json, Type type)`: Parses JSON. Actually it converts a `JsonElement` to your
  application type because the actual low-level parsing of the JSON syntax is performed by the framework. The `Type`
  is important if your adapter supports more than one type: This method only gets called if `supportsType` has
  already returned `true` for that type, so if your adapter only supports a single type, this argument will always be
  the same and you can safely ignore it.
* `JsonElement toJson(T value, Type type)`: Generates JSON. Again this only produces a `JsonElement` because the
  low-level syntax will be handled by the framework.

## Special Tools

* `FieldMustBeNull` only accepts null and may help with keeping different API versions somewhat compatible.
  * It does not allow the field to be missing. Can be combined with `OptionalField` to allow this.
