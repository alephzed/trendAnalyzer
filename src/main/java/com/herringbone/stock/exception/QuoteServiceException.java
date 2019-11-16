package com.herringbone.stock.exception;

public class QuoteServiceException extends RuntimeException {
    public QuoteServiceException() {super();}

    public QuoteServiceException(String message) {super(message);}

    public QuoteServiceException(String message, Exception cause) {super(message, cause);}

    public QuoteServiceException(Throwable cause) {
        super(cause);
    }
}
