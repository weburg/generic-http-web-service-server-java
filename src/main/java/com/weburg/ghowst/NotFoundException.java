package com.weburg.ghowst;

/*
Informational responses (100 – 199)
Successful responses (200 – 299)
Redirection messages (300 – 399)
Client error responses (400 – 499)
Server error responses (500 – 599)

200 OK
201 Resource created

303 Resource created, go here to it, see other

400 Bad Request
401 Unauthorized, you need to authenticate
403 Forbidden, you can't access this even through you're authenticated
405 Method Not Allowed

500 Server error general

*/
// All exceptions are runtime or replace checked exception with runtime equivalent.

// Method problems

// 400, 405
// IllegalArgumentException
// Parameter problems, method not found, etc

// 404
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

// 500
// RuntimeException
// Unrecoverable server errors