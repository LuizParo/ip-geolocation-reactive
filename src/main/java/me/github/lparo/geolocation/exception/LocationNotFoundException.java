package me.github.lparo.geolocation.exception;

/**
 * Custom exception that represents a situation where the location for a given IP address is not found.
 */
public class LocationNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * {@inheritDoc}
     */
    public LocationNotFoundException(String message) {
        super(message);
    }
}
