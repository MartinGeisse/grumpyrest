
# Quick Start

## Import from Maven Central

* Maven:
    ```
    <dependency>
        <groupId>io.github.grumpystuff</groupId>
        <artifactId>grumpyrest</artifactId>
        <version>0.9</version>
    </dependency>
    ```
* Gradle (long):
    ```
    implementation group: 'io.github.grumpystuff', name: 'grumpyrest', version: '0.9'
    ```
* Gradle (short):
    ```
    implementation 'io.github.grumpystuff:grumpyrest:0.9'
    ```

## Using the Jetty Launcher from the Demo

The demo contains a `GrumpyrestJettyLauncher` class that can be used to get an API server up and running with minimal
code. You'll have to copy that class over to your project. It can be used like this:

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

This will launch an embedded Jetty at port 8080 and serve the API just defined.

The launcher class is not part of the library because you'll like have to change the details, such as CORS origin
whitelisting, in a real-world project.

## Using a Servlet Container

When not using a helper like `GrumpyrestJettyLauncher`, grumpyrest ships as a servlet that can be installed into any
web server that acts as a servlet container, such as Jetty or Tomcat. The servlet specification makes this a bit
cumbersome because it insists that the servlet container does not just take a servlet object. Instead, the container
wants to create the servlet object from its class using a no-arg constructor. But the endpoints  of the REST API have
to be specified and passed to this servlet! So the way to go is to write a subclass of `RestServlet` in which you
create and configure the `RestApi` object:

```
public static class MyServlet extends RestServlet {

    public MyServlet() {
        super(buildApi());
    }

    private static RestApi buildApi() {
        RestApi api = new RestApi();
        ...
        return api;
    }

}
```

## Testing the API

Run the main method (or install the servlet in a servlet container), and open http://localhost:8080 in the browser to
get the standard "Hello World" response (with quotes, because it's JSON) or fire a POST request against `/` to see body
parsing in action:

```
curl -X POST -H 'Content-Type: application/json' --data-binary '{"name": "Joe"}' http://localhost:8080
```

## Logging

Logging happens via slf4j as usual. You'll likely have this problem solved because of other libraries that do the same.
If not, or to get more information, see https://www.slf4j.org
