
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
        <version>0.1</version>
    </dependency>
    ```
* Gradle (long):
    ```
    implementation group: 'name.martingeisse', name: 'grumpyrest', version: '0.1'
    ```
* Gradle (short):
    ```
    implementation 'name.martingeisse:grumpyrest:0.1'
    ```

## Web Server

grumpyrest does not include a web server, but instead ships as a servlet that can be installed into any web server that
acts as a servlet container, such as Jetty or Tomcat. For example, here is a "just make it work" dependency list for
Gradle that includes everything that remotely belongs to Jetty (I'd suggest to first include everything, then when some
of the actual features of your application work, remove what you don't need. That is much easier than thinking about
exact dependencies up-front.)

	implementation 'org.eclipse.jetty:jetty-servlets:11.0.15'
	implementation 'org.eclipse.jetty:jetty-webapp:11.0.15'
	implementation 'org.eclipse.jetty:jetty-server:11.0.15'
	implementation 'org.eclipse.jetty:jetty-security:11.0.15'
	implementation 'org.eclipse.jetty:jetty-deploy:11.0.15'
	implementation 'org.eclipse.jetty:jetty-rewrite:11.0.15'
	implementation 'org.eclipse.jetty.http2:http2-server:11.0.15'
	implementation 'org.eclipse.jetty:jetty-annotations:11.0.15'
	implementation 'org.eclipse.jetty:jetty-proxy:11.0.15'
	implementation 'org.eclipse.jetty:jetty-jaas:11.0.15'
	implementation 'org.eclipse.jetty:jetty-plus:11.0.15'
	implementation 'org.eclipse.jetty.toolchain:jetty-test-helper:6.0'

You can then spin up a Jetty server the "embedded way" by creating its server objects in the main method:

```
public static void main(String[] args) throws Exception {
    Server server = new Server();

    ServerConnector connector = new ServerConnector(server);
    connector.setPort(8080);
    server.setConnectors(new Connector[]{connector});

    ServletContextHandler context = new ServletContextHandler();
    context.setContextPath("/");
    context.addServlet(MyServlet.class, "/");

    server.setHandler(new HandlerList(context, new DefaultHandler()));
    server.start();
    server.join();
}
```

This code on its own will give one compiler error because MyServlet is not yet defined. See the next section for that.

## The Application Servlet Class

The servlet specification makes this a bit cumbersome because it insists that the servlet container (such as Jetty)
does not just take a servlet object. Instead, the container wants to create the servlet object from its class using
a no-arg constructor. But the endpoints of the REST API have to be specified and passed to this servlet! So the
way to go is to write a subclass of `RestServlet` in which you create and configure the `RestApi` object:

```
public static class MyServlet extends RestServlet {

    public MyServlet() {
        super(buildApi());
    }

    record MakeGreetingRequest(String name, OptionalField<String> addendum) {}
    record MakeGreetingResponse(String greeting) {}

    private static RestApi buildApi() {
        RestApi api = new RestApi();
        api.addRoute("/", request -> "Hello World!");
        api.addRoute("/make-greeting", request -> {
            MakeGreetingRequest requestBody = request.parseBody(MakeGreetingRequest.class);
            if (requestBody.addendum.isPresent()) {
                return new MakeGreetingResponse("Hello, " + requestBody.name + "! " + requestBody.addendum.getValue());
            } else {
                return new MakeGreetingResponse("Hello, " + requestBody.name + "!");
            }
        });
        return api;
    }

}
```

And that's it. Run the main method, and open http://localhost:8080 in the browser to get the standard "Hello World"
response (with quotes, because it's JSON) or fire a POST request against /make-greeting to see body parsing in action:

```
curl -X POST -H 'Content-Type: application/json' --data-binary '{"name": "Joe"}' http://localhost:8080/make-greeting
```

## Logging

Logging happens via slf4j as usual. You'll likely have this problem solved because of other libraries that do the same.
If not, or to get more information, see https://www.slf4j.org

