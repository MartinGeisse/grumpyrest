# grumpyrest

grumpyrest is a Java REST server framework that does not use annotations, automatic dependency injection or reactive
streams, and minimizes the use of reflection. Instead,
* it leverages the Java type system to indicate the meaning of classes, fields and methods
* it calls constructors to create dependency objects, and passes constructor parameters to inject them
* it uses threads to achieve parallelism, and in particular virtual threads for highly parallel I/O

This is how building an API with grumpyrest looks like:

```
public class GreetingMain {

    record MakeGreetingRequest(String name, OptionalField<String> addendum) {}
    record MakeGreetingResponse(String greeting) {}

    public static void main(String[] args) throws Exception {
        RestApi api = new RestApi();
        api.addRoute(HttpMethod.GET, "/", request -> "Hello World!");
        api.addRoute(HttpMethod.POST, "/", request -> {
            MakeGreetingRequest requestBody = request.parseBody(MakeGreetingRequest.class);
            if (requestBody.addendum.isPresent()) {
                return new MakeGreetingResponse("Hello, " + requestBody.name + "! " + requestBody.addendum.getValue());
            } else {
                return new MakeGreetingResponse("Hello, " + requestBody.name + "!");
            }
        });

        GrumpyrestJettyLauncher launcher = new GrumpyrestJettyLauncher();
        launcher.launch(api);
    }

}
```

Request 1: `{"name": "Joe"}`

Response 1: `{"greeting": "Hello, Joe!"}`

Request 2: `{"name": "Joe", "addendum": "Nice to meet you."}`

Response 2: `{"greeting": "Hello, Joe! Nice to meet you."}`

# Using grumpyrest

See [Quick Start](https://github.com/MartinGeisse/grumpyrest/wiki/howto-quick-start) to get an example project up and running quickly. Also, the
grumpyrest-demo subproject shows how to use it.

It is not on Maven Central yet, unfortunately, so I cannot just give the Maven coordinates here. See the quick
start on how to use it anyway.

# Further Reading

The [howto file](https://github.com/MartinGeisse/grumpyrest/wiki/howto) explains common tasks.

Read the [concept file](https://github.com/MartinGeisse/grumpyrest/wiki/concept) to understand the idea behind grumpyrest.
