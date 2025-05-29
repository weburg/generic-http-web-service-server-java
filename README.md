# Generic HTTP Web Service Server in Java (GHoWSt)

## An example server providing the Web service and an HTML-only static client

> [!CAUTION]
> This server and its code isn't meant to run in a production or otherwise
> public environment, as it lacks enough error checking and restrictions to be
> safe. It's meant to run locally for prototyping and example purposes only.

### Design goals

- Map incoming HTTP requests by looking at resource (from the URL) and verb
  (HTTP method) or custom verbs (URL) to perform the appropriate action. This
  is similar to REST-like frameworks.
- Also similar to most REST-like frameworks, map exceptions to HTTP response
  status codes and vice versa.
- Provide an embedded, easy to run Web service using a basic MVC structure.
  - Stick to simple files plus JavaBeans (models), JSP and JSON (views), and
    Java servlets (controllers).
- Demonstrate that a basic HTML page containing no JavaScript can GET and POST
  natively via the service.
- Have the Web service detect the response format the client will accept and
  send back JSON to programmatic clients or HTML to browsers by looking at the
  accept header. The HTML client uses the same URLs to interact with the
  service but its data gets formatted to human-readable HTML.

### Capabilities

- Server allows creating, updating, reading, listing, and deleting example
  Engine and Image resources. Creating, reading, listing, and playing Sound
  resources on the server and in a browser is also possible.
- Engine resource is an object with string and numeric data, while Image and
  Sound resources show binary file upload abilities through HTML based file
  uploads and viewing/playing resources.

The client can make calls easily to the server like so, once it has a `ws` proxy
object to the server. Here is some pseudocode:

`id = ws->createEngine(engine)`

or

`engines = ws->getEngines()`

### Limitations

- See project Issues for current limitations and plans.
- The HTML-only client cannot delete or modify content. For that ability, refer
  to the related JavaScript client project which runs in a browser, or other
  programmatic clients.

### Running the server

Ensure Java JDK 11 or better and Maven 3 or better are installed.

If using the CLI, ensure you are in the project directory. Run:

`mvn compile exec:exec`

If using an IDE, you should only need to run the below class after compiling:

`src/main/java/Main.java`

The server creates a directory called `.HttpWebService` in your user home
directory to write serialized resources and other created files into.

You can now navigate to http://localhost:8081 to view the server and browse any
created content, or create new content with the HTML forms interface.

Leave the server running when using the related projects to create content.
Those projects can run on another machine, or the same machine. Just remember to
change localhost to the desired server as required.

### Running the tests

To run unit tests only:

`mvn test`

To run unit and integration tests:

`mvn verify`

### Related projects

Refer to other grouped GHoWSt projects for various clients enabling native and
dynamic method invocation examples.