package me.github.lparo.geolocation.api.validation;

import me.github.lparo.geolocation.exception.InvalidIpException;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpValidatorTest {

    @InjectMocks
    private IpValidator ipValidator;

    @Mock
    private InetAddressValidator validator;

    @Test
    void validateIp_whenCalledWithNullIp_shouldReturnMonoWithEmptyMono() {
        StepVerifier.create(ipValidator.validateIp(null))
                .consumeSubscriptionWith(subscription -> verifyNoInteractions(validator))
                .verifyComplete();
    }

    @Test
    void validateIp_whenCalledWithEmptyIp_shouldReturnMonoWithEmptyMono() {
        StepVerifier.create(ipValidator.validateIp(""))
                .consumeSubscriptionWith(subscription -> verifyNoInteractions(validator))
                .verifyComplete();
    }

    @Test
    void validateIp_whenCalledWithValidIp_shouldReturnIt() {
        final String ip = "127.0.0.1";

        when(validator.isValidInet4Address(ip)).thenReturn(TRUE);

        StepVerifier.create(ipValidator.validateIp(ip))
                .consumeSubscriptionWith(subscription -> {
                    verify(validator, times(1)).isValidInet4Address(ip);
                    verifyNoMoreInteractions(validator);
                })
                .expectNext(ip)
                .verifyComplete();
    }

    @Test
    void validateIp_whenCalledWithInvalidIp_shouldReturnMonoWithError() {
        final String ip = "invalid";

        when(validator.isValidInet4Address(ip)).thenReturn(FALSE);

        StepVerifier.create(ipValidator.validateIp(ip))
                .consumeSubscriptionWith(subscription -> {
                    verify(validator, times(1)).isValidInet4Address(ip);
                    verifyNoMoreInteractions(validator);
                })
                .consumeErrorWith(error -> {
                    assertThat(error, isA(InvalidIpException.class));
                    assertThat(error.getMessage(), is("invalid IP format: " + ip));
                })
                .verify();
    }
}