
# Using Dependency Injection Anyway

Let's say you have a project in which you want to use (or are already using) a dependency injection framework. Can you
use grumpyrest in such a project? Absolutely!

When we say that grumpyrest "does not use DI", we mean that it is DI-agnostic, not DI-averse. Just like you can use a
`FileInputStream` in your project (which does not itself use DI either), you can use grumpyrest. In fact, since
grumpyrest is not dependent on a specific DI framework like some other REST frameworks are, you won't have to bridge
between two such frameworks. Instead, you will have to write a small amount of glue code that integrates grumpyrest
into your application (and DI framework), which tends to be much easier and much more manageable.

## Guice (with Guice Servlet) Example

Here is an example how the glue code would look in Guice and Guice Servlet.

First, we need the typical boilerplate code for a minimal application with Guice Servlet. That is, we set up the
servlet container and add `GuiceFilter` and `GuiceServletContextListener` to it. If you have never done this before,
I'd recommend looking up the examples from the Guice project: [this](https://github.com/google/guice/wiki/Servlets)
and [this](https://github.com/google/guice/wiki/ServletModule)

The `GuiceServletContextListener` must define the Guice modules to use. As usual, you may choose to use many small
or one big module. You may also choose to register all bindings inside the `ServletModule`, or use a separate module
for all "normal" bindings and only bind servlets and filters in the `ServletModule`. In this example, I'll go
with a `ServletModule` that only registers a single servlet, and a separate `AbstractModule` subclass that registers
all other dependencies.

Let's start with the servlet module because little inside there is grumpyrest specific:

```
public class MyServletModule extends ServletModule {
    protected void configureServlet() {
        serve("/*").with(MyServlet.class);
    }
}
```

We use a subclass of `RestServlet` again, like in the DI-less example, but this time it is created by Guice and can
define dependencies that will be injected:

```
@Singleton
public class MyServlet extends RestServlet {
    @Inject
    public MyServlet(RestApi api) {
        super(api, RequestPathSourcingStrategy.STARTING_WITH_CONTEXT_PATH);
    }
}
```

As you can see, the `RestApi` gets injected here. Of course, we could create it in the `MyServlet` constructor, but
since we are already going full DI for decoupling, we'll have the `RestApi` injected so its code does not depend on
servlet specifics.

We still need the code that defines the actual API endpoints. We'll use a Guice `Provider`for that:

```
@Singleton
public class RestApiProvider implements Provider<RestApi> {
    public RestApi get() {
        RestApi api = new RestApi();
        // ... define the API endpoints here, register JSON type adapters, whatever ...
        // Also, you can of course have things like type adapter contributors injected into the `RestApiProvider`
        // for further decoupling.
        return api;
    }
}
```

In the main Guice module that contains all our bindings, we need to tell Guice about that provider:

```
public class MyGuiceModule extends AbstractModule {
    protected void configure() {
        // ... other bindings ...
        bind(RestApi.class).toProvider(RestApiProvider.class);
        // ... other bindings ...
    }
}
```

And that's it! grumpyrest is now running in your Guice application.

## Request-Scoped Handlers

A typical pattern is to perform the request handling in short-lived, request-scoped objects. Such objects are newly
created for each request, and discarded after the request has been finished. The advantage of this pattern is there
is much less danger of accidentally leaking data from one request to another when a handler contains a bug. It also
allows to store request-scoped data such as the authenticated user in those objects. Finally, frameworks will
usually inject dependencies into the request-scoped objects too, and decide on a per-dependency basis which
dependencies are global and should be shared, and which ones are request-scoped and have to be created for each
request.

If one tried to transfer this pattern to grumpyrest, one might want to have the `Handler` re-created for each request.
This won't work, because the `Handler` is long-lived by design. But you wouldn't want to write boilerplate code into
each handler that just creates a request-scoped "actual handler" and call it. Fortunately, you don't have to: This
boilerplate code can again be generalized. Just as with launching the application, the details will depend on the DI
framework. I'll explain it for Guice:

The first step is to move the handler code to a proper class, in case it is currently in a lambda. This of course
inflates the code a bit, but lambdas would be globally scoped. Also, a proper class can declare dependencies to be
injected. This class should implement `SimpleHandler` (or `ComplexHandler` if needed), and should be marked as
request-scoped so Guice creates a new instance per request:

```
@RequestScoped
public class MyHandler implements SimpleHandler {
    public Object handle(Request request) throws Exception {
        // ...
    }
}
```

Such a handler could only be mounted in a `RestApi` if you went and created a global instance, which is exactly what
you don't want to do. Instead, a wrapping handler is mounted that creates a new instance of the actual handler for
each request. Since you don't want to have Guice inject `Provider`s for all your request classes, you'll have to
have the `Injector` injected into the `RestApiProvider`:

```
@Singleton
public class RestApiProvider implements Provider<RestApi> {

    private final Injector injector;
    
    @Inject
    public RestApiProvider(Injector injector) {
        this.injector = injector;
    }

    // ...
}
```

You can then define your handler-wrapping method:

```
private SimpleHandler wrap(Class<? extends SimpleHandler> wrappedClass) {
    return request -> injector GET wrappedClass .handle(request);
}
```

You can then mount your handler classes by wrapping them. Instead of writing

```
api.addRoute(HttpMethod.GET, "/", new MyRequestHandler());
```

you should now write:

```
api.addRoute(HttpMethod.GET, "/", wrap(MyRequestHandler.class));
```

Your handler is now request-scoped.
