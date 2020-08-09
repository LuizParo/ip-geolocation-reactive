package me.github.lparo.geolocation.exception;

/**
 * Custom exception that represents a situation where the given IP address is not valid.
 */
public class InvalidIpException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    public InvalidIpException(String message) {
        super(message);
    }
}
