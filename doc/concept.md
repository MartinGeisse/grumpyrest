## Core Idea

Particular differences with typical REST frameworks:
* To mount an endpoint to a URL, you do not annotate the handler method. Instead, you call a mount method and pass the
  handler as a parameter.
* To access request properties such as URL path parameters, querystring parameters or the request body, you do not
  declare handler parameters. Instead, you call a method. This method takes a specification of the expected type
  and performs validation before returning its data.
* Request/response bodies are typically expected to be JSON. To declare how the request body is parsed from JSON, as
  well as validations to perform, you use appropriate Java types that validate their parameters / fields in their
  constructor. Similarly, to declare how the response objects are mapped to JSON, you use appropriate Java types with
  a well-defined JSON mapping. Mappings in both directions can be registered from outside the data-holding classes to
  support existing classes.
* This includes nullable and optional fields: Appropriate generic wrapper types are provided to define nullable and/or
  optional fields. Java's null references are NOT considered equivalent to JSON-null nor to absent JSON properties,
  and having null references on the Java side of JSON mapping is considered an error to avoid any ambiguities.
* No type / field is nullable or optional by default. No default values will be inserted for missing fields. No
  coercion from number to string or from string to number will be done by default. An empty JSON object, empty JSON
  array, false, 0, null and missing fields are all different things that are not considered equal. If you want to
  bend these rules for specific types or fields, use an appropriate type for that which defines a relaxed mapping.
  If you want to bend these rules for all of your JSON mapping, replace the built-in type adapters by your own which
  define a relaxed mapping.

## What about performance?

It _is_ anticipated that grumpyrest may provide tools to tackle potential performance problems, which rely on
reactive programming and/or code generation. The goal of grumpyrest is not to totally avoid these things internally,
but to allow the developers using it to totally avoid them in their application code.

This means:
* We will not add anything like that to grumpyrest until it is perfectly clear that it solves a performance problem
  in modern Java which, for example, virtual threads cannot handle.
* None of this affects application code, unless you choose to use it in application code (because the performance
  problem lies there instead of inside grumpyrest).
* Any such features can be turned of to exclude them as a potential source of bugs, as well as to single-step debug
  into grumpyrest to understand what happens inside (e.g. to track down a specific bug). We all know what single-step
  debugging into highly asynchronous / reactive or generated code is like.

## JSON Mapping

A mapping exists between Java records and JSON objects:

    record CategoryLink(int id, String name) {}
    record ProductResponse(CategoryLink category, String name, String description, int unitPrice) {}
    new ProductResponse(new CategoryLink(2, "Widgets"), "Left-handed Hammer", "Excellent choice for left-handed people!", 10)

<->

    {
      "category": {
        "id": 2,
        "name": "Widgets"
      },
      "name": "Left-handed Hammer",
      "description": "Excellent choice for left-handed people!",
      "unitPrice": 10
    }

A mapping for non-record objects could be defined, but the strong guarantees about their shape that come from the
language make the mapping very easy. Also, I just like that they are immutable.

JSON arrays can be mapped to Guava's ImmutableList:

    ImmutableList.of(1, 2, 3)

<->

    [1, 2, 3]

Again, normal List / ArrayList could be mapped too, but I just like ImmutableList.

JSON primitives are mapped to Java's built-in types, both boxed and unboxed. No coercion takes place, so for example
the number 123 cannot be parsed into a String-typed field (unless you override the type adapter for type String to
accept numbers).

### Nullable and Optional Properties

Object properties are neither nullable nor optional by default. That is, a missing property in JSON causes an error,
and so does a property that is null in JSON. The Java record can use the type NullableField to allow null in JSON,
but on the Java side, it is still not null but a NullableField without a value. This works similar to java.util.Optional
but IDEs complain if you actually use java.util.Optional except in a few specific places, and besides that nullable
properties and optional properties are different things, So I defined my own types.

Similar to nullable fields, the type OptionalField defines a property which may be absent from its record. Again, I
didn't use java.util.Optional because that type doesn't seem to be meant for using it that way. This type can only be
used in records since a top-level value cannot be just absent, and for JSON array elements, an "optional" property can
never be parsed as absent and acts as a poor man's filter during serialization. So there is no added value in allowing
this in JSON arrays. Like NullableField, a missing optional property corresponds to an OptionalField object without a
value on the Java side, not to a field that is directly null in the containing record.

As you may have guessed by now, Java's null reference never appears in JSON mapping: The parser won't produce it and
the serializer will throw an exception if you give it a record with a field that is null. Java's null references are
just too easy to get wrong, and especially, are ambiguous with respect to the fact that a property in JSON may be
nullable or optional. It may even be both: An OptionalField<NullableField<T>> on the Java side maps to a JSON field
that may be absent, or null, or have a value.

Unknown properties in records cause an error too. A type adapter which collects extra properties in a Map<> may be
defined in the  future.

### Validation

Other JSON mapping frameworks either define how they do validation after parsing, or say that validation is not their
business. In contrast, I do not think that it is possible to split parsing and validation into two different phases in
any meaningful way. To split them, there would have to be a Java type that results from parsing that represents the
data in a parsed but unvalidated state, which is then passed to validation. This implies one of three possible things:

* either that such data is defined as Java classes which routinely do not validate their state in their constructor,
  so they are happy with being in an inconsistent state. It might be even worse and such classes might not just delay
  validation of their state until later, but they might even rely on external validation rules to do so, i.e. they do
  not have an independent notion of what it means for their state to be "valid".
* or the classes used for JSON mapping are built outright as a set of dumb data containers, with the sole purpose of
  making the JSON data accessible from Java code, and getting away from those classes agin as fast as possible. This
  adds unnecessary complexity (there is a whole extra layer in your code without any real purpose) and validation is
  actually harder now, because you have to define rules for your JSON but express them as rules in Java. This is
  one of the reasons we have OptionalField and NullableField as two separate classes, because good luck if you have a
  null reference in Java and when building an error message you want to know if the client sent null or forgot that
  field.
* or you aren't even defining rigorous validation rules, and impose whatever meaning is closest on the incoming JSON
  data. Code that does this will happily ignore extra fields in records, coerce numbers to strings, give missing
  properties and null properties the same meaning, and so on. This does not mean that your code is unsafe -- safety
  is implemented by the code that does something with that data, and is rarely dependent on JSON validation. However,
  I think that being careless about validation will steer you directly into hell when it comes to versioning you
  JSON API payloads, and making changes to them, because the clients calling your API are almost guaranteed to be
  dependent on behavior of your API in cases that you never defined.

The bottom line is that we don't have any validation after parsing. Rather, validation is part of parsing. If signing
up to your service requires users to be at least 18 years old, validate (age >= 18) in the constructor of your
SignUpRequestBody. If the username cannot be empty, check that too in the same constructor. Alternatively, if usernames
must be specific in many places and must be validated to be nonempty (and possibly at least 5 characters, and not
contain ASCII control characters nor offensive words), define a UserName type that checks these things in its
constructor. If you have an existing typpe chose constructor cannot be changed, wrap it in a type that defines
its validity rules. Or, as the last resort, you can define a custom type adapter and register it in the JsonRegistry
that defines how a type gets parsed and serialized. But there is no validation after parsing, period.
