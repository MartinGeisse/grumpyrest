
# Quick Start

## Publish to Maven Local

grumpyrest is not yet available on Maven Central. To use it, clone the repo:

    git clone https://github.com/MartinGeisse/grumpyrest.git

then run the following command in its main folder (the one containing the `settings.gradle` file):

    ./gradlew publishToMavenLocal

This will build and publish the libraries to your local Maven repo in `~/.m2`

You can then refer to it like this:

* Maven:
    ```
    <dependency>
        <groupId>name.martingeisse</groupId>
        <artifactId>grumpyrest</artifactId>
        <version>0.5</version>
    </dependency>
    ```
* Gradle (long):
    ```
    implementation group: 'name.martingeisse', name: 'grumpyrest', version: '0.5'
    ```
* Gradle (short):
    ```
    implementation 'name.martingeisse:grumpyrest:0.5'
    ```

## Using the pre-built Jetty Launcher

The `name.martingeisse:grumpyrest-jetty-launcher:...` package contains a helper class to get an API server up and
running with minimal code. This package gets build and locally published together with the main grumpyrest package when
using the above commands. You'll have to add it as a separate dependency in Maven or Gradle. It can be used like this:

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

This will launch an embedded Jetty at port 8080 and server the API just defined.

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
