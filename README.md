# Generic HTTP Web Service Server in Java (GHoWSt)

## An example server providing the Web service and an HTML-only static client in one.

### Design goals

- Map incoming HTTP requests by looking at resource (from the URI) and verb (HTTP method) to perform the appropriate action.
- Provide an embedded, easy to run Web service using a basic MVC structure.
  - Stick to simple files plus JavaBeans (models), JSP and JSON (views), and Java servlets (controllers).
- Demonstrate that a basic HTML page containing no JavaScript can GET and POST natively via the service.

### Capabilities

- Server allows creating, updating, reading, listing, and deleting example Engine and Photo resources. Playing a sound on the server is also possible.

### Limitations

- See project Issues for current limitations and plans.

### Running the server

Run `src/main/java/Main.java`.