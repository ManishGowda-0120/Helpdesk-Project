package com.helpdesk.helpdesk.exception;

public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(String message) {
        super(message);
    }
}

// What this does
//Custom error for your app
//Instead of returning null, we throw this