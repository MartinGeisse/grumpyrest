
# Overriding Type Converters

You might experience a situation in which you have a rather generic JSON type converter that handles many types, and
you want to handle one of those types in a special way but still keep the generic one for all the other types it can
handle. This can happen on both the serialization and deserialization sides, but is more common for deserialization
because that operates on variable _types_ -- possibly parameterized ones ("generics") -- and so can distinguish types
based on type arguments. You might also run into a situation in serialization in which you want to override the
default behaviour for specific _values_. This HOWTO explains how to achieve that.

## Overriding specific types during deserialization

Here is a real-world example: The standard type converters include a deserializer for nullable values. This converter
supports the type `Nullable<...>` with any concrete type argument, for example `Nullable<String>`.

Now suppose you have a domain type called `DisplayName` that wraps a pre-trimmed, non-empty string. `DisplayName`
would typically be implemented as a Java `record`, and that would also allow Grumpyjson to auto-generate a serializer
and deserializer for it. `DisplayName` would inspect its argument to be pre-trimmed and non-empty, and throw an
exception if not. That exception would cause Grumpyjson to report an error for the field with type `DisplayName`.

Furthermore, Grumpyjson assumes that records are simple data containers, and that an `IllegalArgumentException` from a
record constructor indicates a problem with its contents, not a program-internal error, and so contains information
that should be responded to the REST client. If the display name is empty, or not pre-trimmed, then a 400 response
will be sent back to the client, indicating the path of the `DisplayName`-typed field and the message from the
`IllegalArgumentException`. Finally, the standard type converter for nullable fields will match a field of type
`Nullable<DisplayName>`, and recognize JSON `null` -- turning it into `NullableField.ofNull()` -- and pass any other
value to the deserializer for `DisplayName`. So far, there is absolutely nothing wrong about this approach.

Now suppose that another requirement pops up: If the client sends a `DisplayName` that is _not_ pre-trimmed, then it
should be trimmed automatically, and if it is empty (possibly becoming empty by trimming), then it should be treated as
`null`. Now we have a problem: While we could easily change the record constructor of `DisplayName` to trim its
arguments, we can not make it turn the enclosing `NullableField` into a nullish state if the string is empty. We need
to override the converter for `NullableField<DisplayName>` to do that -- but we still want the original behaviour
for `NullableField<...>` with any other type argument.

Doing that is actually quite simple. The deserializer registry allows to register a deserializer for an arbitrary
type, including generics with specific, concrete type arguments such as `NullableField<DisplayName>`. For that, we
have to create class implementing `JsonDeserializer` and have it return `true` for that, and only that, type in its
`supportsTypeForDeserialization` method:

```
@Override
public boolean supportsTypeForDeserialization(Type type) {
    if (type instanceof ParameterizedType p && p.getRawType().equals(NullableField.class)) {
        Type[] typeArguments = p.getActualTypeArguments();
        return typeArguments.length == 1 && typeArguments[0].equals(DisplayName.class);
    }
    return false;
}
```

The registry will now consider this converter capable of deserializing a `NullableField<DisplayName>`. Since it returns
`false` for all other types, the registry won't use it for those.

The second step is to make the registry _prefer_ it over the generic `NullableField<...>` converter. This happens by
registering it _after_ the generic one: For any type to register, the registry will ask all converters in the order
from last-registered to first-registered, and take the first one that can handle that type. This supports the way
converters are typically registered: Generic ones first, from a centralized place, and specific ones later from the
individual feature modules.

Finally, you'll have to remember that your own converter is now resposible for the whole of `NullableField<DisplayName>`
which means that it will not only have to trim that string and map it to `NullableField.ofNull()` if empty, but it
will also have to accept JSON `null` and map _that_ to `NullableField.ofNull()` too.

## Overriding specific types during serialization

Handling specific types during serialization works quite similar to the deserialization case. However, you have to be
aware of the fact that serialization operates purely on the run-time classes of the objects involved, not the static
types of any variables. This means that the serializer registry cannot distinguish between `NullableField<DisplayName>`
and `NullableField<String>`, because it doesn't see that type at all -- it just sees the class object
`NullableField.class`. 

If you still want to distinguish those two cases, a custom serializer might take the value contained in that
`NullableField` and change its behavior based on whether that object is a `DisplayName` or a `String`. Skip to the
next section if that is what you want to do. Keep in mind, however, that `NullableField.ofNull()` does not contain
any value to make that distinction.

There are still case in which a generic serializer can be overridden by a specific serializer -- they are just much
rarer than for deserialization. One such case would be for specific subclasses, with a more generic serializer
already registered for the superclass. The approach is very similar to the one for deserialization, except that in the
`supportsClassForSerialization` method, you'll inspect class objects, not static types. Checking for a subclass is
straightforward:

```
@Override
public boolean supportsClassForSerialization(Class<?> clazz) {
    return MyMoreSpecificSubclass.class.isAssignableFrom(clazz);
}
```

## Overriding specific values during deserialization








As a real-world example, 

*     <li>When the registrable for a specific key is requested, the registered registrables are checked last-to-first,
*     so the last-registered registrable that supports the key will be returned. This allows to register generic
*     (multi-key) registrables first, and override them for specific keys later. In particular, if the keys are
*     {@link Type}s, then the registrable for all <code>List&lt;T&gt;</code> types can be added early, and overridden
*     for specific types like <code>List&lt;String&gt;</code> later.</li>
