
# Querystring Parameters

First, define the complete set of accepted querystring parameters for a handler as a record. For example,
if you want your handler to accept a string-typed `name` parameter, an integer-typed `age` parameter and an
optional boolean `includeDetails` parameter, that record would look like this:

```
record MyHandlerQuerystringParameters(String name, int age, OptionalField<Boolean> includeDetails) {}
```

You can then obtain the actual arguments for these parameters from the `Request`:

```
MyHandlerQuerystringParameters parameters = request.parseQuerystring(MyHandlerQuerystringParameters.class);
```

The supported field types use the same registry as path parameters.

# Notes

* optional parameters are supported using the same `OptionalField<>` as in JSON. The `NullableField<>`
  type is not supported here because there is no meaningful way to pass `null` as an argument. A querystring
  such as `a=&b=5` would pass the empty string, not `null`, for parameter `a`.
* default values for optional parameters must be applied when getting the value from the `OptionalField` object,
  not at the time the querystring gets parsed. Alternatively, the `MyHandlerQuerystringParameters` constructor
  may check which parameters are present and provide default values.