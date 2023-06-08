
# Generics vs. JSON support

Our type adapters can support generic types. For example, a list adapter may support all kinds of List<...>. This
requires to use a polymorphic type adapter, and thus refer to the registry at run time (either with the concrete
static type passed when calling the adapter, or for serialization, the classes of the actual objects found in the list).

(Alternatively, we could generate monomorphic type adapters at run-time, but there is little advantage in that over a
single polymorphic one.)

What kinds of list can be supported?
- List<SupportedType>, for example, List<String> -- the element type can be found in the registry
- List<List<SupportedType>> -- the element type may be polymorphic again, so the correct type token must be passed
  to the elemment adapter
- List<T>, List<List<T>>, List<?>, List<? extends ConcreteType> -- these won't work, because we have an unbound type
  variable. It's like asking: "Without knowing the value of x, what is x+1?" At best, we might evade the problem if
  the type variable isn't actually used in a field, or is used in a field without any run-time value instance. In the
  analogy, we would know the value of 0*x even without knowing x, but this is only true for degenerate cases.
  We probably do not have to detect this case explicitly -- in a non-degenerate case, this will end up asking the
  registry for a type adapter for type "T" which the registry won't have.
- GenericArrayType types, i.e. List<String>[], T[], List<T>[] -- the first one would work. We simply avoid this case by
  not using arrays, but ImmutableList.

What we have to do is therefore:
- support only Class<?> and ParameterizedType, and the latter only if recursively supported
- provide run-time access to the registry
- use the context type to map ParameterizedType field types with type variables to ParameterizedType with concrete types
