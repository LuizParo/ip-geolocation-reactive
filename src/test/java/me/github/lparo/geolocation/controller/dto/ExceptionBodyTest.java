package me.github.lparo.geolocation.controller.dto;

import me.github.lparo.geolocation.exception.InvalidIpException;
import me.github.lparo.geolocation.exception.LocationNotFoundException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class ExceptionBodyTest {
    private static final String MESSAGE = "any message";
    private static final String LOCATION_NOT_FOUND_EXCEPTION_CLASS = LocationNotFoundException.class.getName();
    private static final String INVALID_IP_EXCEPTION_CLASS = InvalidIpException.class.getName();
    private static final String EXCEPTION_CLASS = Exception.class.getName();

    @Test
    void fromThrowable_whenCalledWithParameters_andThrowableClassNameIsLocationNotFoundException_shouldCreateDtoWithStatusNotFound() {
        final ExceptionBody exceptionBody = ExceptionBody.from(LOCATION_NOT_FOUND_EXCEPTION_CLASS, MESSAGE);

        assertThat(exceptionBody, notNullValue());
        assertThat(exceptionBody.getStatus(), is(NOT_FOUND));
        assertThat(exceptionBody.getMessage(), is(MESSAGE));
    }

    @Test
    void fromThrowable_whenCalledWithParameters_andThrowableClassNameIsInvalidIpException_shouldCreateDtoWithStatusBadRequest() {
        final ExceptionBody exceptionBody = ExceptionBody.from(INVALID_IP_EXCEPTION_CLASS, MESSAGE);

        assertThat(exceptionBody, notNullValue());
        assertThat(exceptionBody.getStatus(), is(BAD_REQUEST));
        assertThat(exceptionBody.getMessage(), is(MESSAGE));
    }

    @Test
    void fromThrowable_whenCalledWithParameters_andThrowableClassNameIsAnyOtherException_shouldCreateDtoWithStatusInternalServerError() {
        final ExceptionBody exceptionBody = ExceptionBody.from(EXCEPTION_CLASS, MESSAGE);

        assertThat(exceptionBody, notNullValue());
        assertThat(exceptionBody.getStatus(), is(INTERNAL_SERVER_ERROR));
        assertThat(exceptionBody.getMessage(), is(MESSAGE));
    }
}