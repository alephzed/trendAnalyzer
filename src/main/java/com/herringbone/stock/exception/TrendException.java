package com.herringbone.stock.exception;

public class TrendException extends RuntimeException {
    public TrendException() {super();}

    public TrendException(String message) {super(message);}

    public TrendException(String message, Exception cause) {super(message, cause);}

    public TrendException(Throwable cause) {
        super(cause);
    }
}
