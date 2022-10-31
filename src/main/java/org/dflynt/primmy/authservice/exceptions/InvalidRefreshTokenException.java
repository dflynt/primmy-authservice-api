package org.dflynt.primmy.authservice.exceptions;

public class InvalidRefreshTokenException extends Exception{

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
