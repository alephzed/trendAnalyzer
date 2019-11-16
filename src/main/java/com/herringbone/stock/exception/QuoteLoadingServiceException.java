package com.herringbone.stock.exception;

public class QuoteLoadingServiceException extends RuntimeException {
    public QuoteLoadingServiceException() {super();}

    public QuoteLoadingServiceException(String message) {super(message);}

    public QuoteLoadingServiceException(String message, Exception cause) {super(message, cause);}

    public QuoteLoadingServiceException(Throwable cause) {
        super(cause);
    }
}
