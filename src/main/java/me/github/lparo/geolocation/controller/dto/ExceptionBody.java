package me.github.lparo.geolocation.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import me.github.lparo.geolocation.exception.InvalidIpException;
import me.github.lparo.geolocation.exception.LocationNotFoundException;
import org.springframework.http.HttpStatus;

/**
 * DTO representation of any {@link Throwable} that was thrown during a request and needs to be sent within a response body.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionBody {

    @JsonIgnore
    HttpStatus status;
    String message;

    /**
     * Creates a DTO representation of a {@link Throwable}. It receives the throwable class name and a message, and maps
     * which HTTP status should the it be mapped to.
     *
     * @param throwableClass the throwable class to determine which HTTP status it maps.
     * @param message the message to be be used as in the response body.
     *
     * @return an {@link ExceptionBody} representing the original {@link Throwable} that was thrown during the request.
     */
    public static ExceptionBody from(String throwableClass, String message) {
        if (LocationNotFoundException.class.getName().equals(throwableClass)) {
            return new ExceptionBody(HttpStatus.NOT_FOUND, message);
        }

        if (InvalidIpException.class.getName().equals(throwableClass)) {
            return new ExceptionBody(HttpStatus.BAD_REQUEST, message);
        }

        return new ExceptionBody(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
