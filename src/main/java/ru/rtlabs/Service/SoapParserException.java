package ru.rtlabs.Service;

public class SoapParserException extends Exception {
    private final ErrorCode errorCode;

    public SoapParserException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public SoapParserException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}