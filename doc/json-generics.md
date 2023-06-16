
# Generics vs. JSON support

TODO:
- should optimize non-generic records, as well as fields whose types do not use type variables (performance)
- a record is processed with a concrete Type (that should not contain type variables, see above). At run-time, a field
  whose type contains a type variable must match that TV's name with the declared type parameters of the type's raw
  type (which should always be a class), then take the concrete type parameter at the same index. Using this, all
  type variables in the field's type can be bound and the field's type becomes concrete. If a type variable cannot
  be index-named, or cannot be name-matched -> error. If the bound type still contains a type variable -> the registry
  will not find a type adapter for this.
