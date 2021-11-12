package io.qbeat.exceptions;


/**
 * Can be thrown when configuration cannot be read
 */
public class ConfigurationReadException extends RuntimeException {

    public ConfigurationReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
