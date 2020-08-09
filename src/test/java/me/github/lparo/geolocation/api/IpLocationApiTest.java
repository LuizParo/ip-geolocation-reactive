package me.github.lparo.geolocation.api;

import me.github.lparo.geolocation.api.validation.IpValidator;
import me.github.lparo.geolocation.domain.City;
import me.github.lparo.geolocation.domain.Country;
import me.github.lparo.geolocation.domain.IpCityLocation;
import me.github.lparo.geolocation.domain.IpCountryLocation;
import me.github.lparo.geolocation.domain.State;
import me.github.lparo.geolocation.exception.InvalidIpException;
import me.github.lparo.geolocation.service.IpCityLocationService;
import me.github.lparo.geolocation.service.IpCountryLocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpLocationApiTest {
    private static final String VALID_IP = "217.138.219.147";
    private static final String INVALID_IP = "invalid";

    private IpLocationApi ipLocationApi;

    @Mock
    private IpValidator ipValidator;

    @Mock
    private IpCityLocationService ipCityLocationService;

    @Mock
    private IpCountryLocationService ipCountryLocationService;

    private final String hostMachinePublicIp = "217.138.219.150";

    @BeforeEach
    void setUp() {
        this.ipLocationApi = new IpLocationApi(ipValidator, ipCityLocationService, ipCountryLocationService, hostMachinePublicIp);
    }

    @Test
    public void getCityLocation_whenIpIsInvalid_shouldReturnMonoWithError() {
        when(ipValidator.validateIp(INVALID_IP)).thenReturn(Mono.error(new InvalidIpException("error")));

        StepVerifier.create(ipLocationApi.getCityLocation(INVALID_IP))
                .consumeSubscriptionWith(subscription -> {
                    verify(ipValidator, times(1)).validateIp(INVALID_IP);
                    verifyNoMoreInteractions(ipValidator);

                    verifyNoInteractions(ipCityLocationService);
                    verifyNoInteractions(ipCountryLocationService);
                })
                .consumeErrorWith(error -> {
                    assertThat(error, isA(InvalidIpException.class));
                    assertThat(error.getMessage(), is("error"));
                })
                .verify();
    }

    @Test
    public void getCityLocation_whenIpValidationReturnsEmptyMono_shouldGetTheCityLocationOfTheHostMachinePublicIp() {
        final IpCityLocation ipCityLocationDomain = createIpCityLocationDomain();

        when(ipValidator.validateIp(null)).thenReturn(Mono.empty());
        when(ipCityLocationService.getCityLocationForIp(hostMachinePublicIp)).thenReturn(Mono.just(ipCityLocationDomain));

        StepVerifier.create(ipLocationApi.getCityLocation(null))
            .consumeSubscriptionWith(subscription -> {
                verify(ipValidator, times(1)).validateIp(null);
                verify(ipCityLocationService, times(1)).getCityLocationForIp(hostMachinePublicIp);

                verifyNoMoreInteractions(ipValidator);
                verifyNoMoreInteractions(ipCityLocationService);

                verifyNoInteractions(ipCountryLocationService);
            })
            .expectNext(me.github.lparo.geolocation.controller.dto.IpCityLocation.fromDomain(ipCityLocationDomain))
            .verifyComplete();
    }

    @Test
    public void getCityLocation_whenIpIsPresent_shouldGetItsTheCityLocation() {
        final IpCityLocation ipCityLocationDomain = createIpCityLocationDomain();

        when(ipValidator.validateIp(VALID_IP)).thenReturn(Mono.just(VALID_IP));
        when(ipCityLocationService.getCityLocationForIp(VALID_IP)).thenReturn(Mono.just(ipCityLocationDomain));

        StepVerifier.create(ipLocationApi.getCityLocation(VALID_IP))
                .consumeSubscriptionWith(subscription -> {
                    verify(ipValidator, times(1)).validateIp(VALID_IP);
                    verify(ipCityLocationService, times(1)).getCityLocationForIp(VALID_IP);

                    verifyNoMoreInteractions(ipValidator);
                    verifyNoMoreInteractions(ipCityLocationService);

                    verifyNoInteractions(ipCountryLocationService);
                })
                .expectNext(me.github.lparo.geolocation.controller.dto.IpCityLocation.fromDomain(ipCityLocationDomain))
                .verifyComplete();
    }

    @Test
    public void getCountryLocation_whenIpIsInvalid_shouldThrowAnError() {
        when(ipValidator.validateIp(INVALID_IP)).thenReturn(Mono.error(new InvalidIpException("error")));

        StepVerifier.create(ipLocationApi.getCountryLocation(INVALID_IP))
                .consumeSubscriptionWith(subscription -> {
                    verify(ipValidator, times(1)).validateIp(INVALID_IP);
                    verifyNoMoreInteractions(ipValidator);

                    verifyNoInteractions(ipCityLocationService);
                    verifyNoInteractions(ipCountryLocationService);
                })
                .consumeErrorWith(error -> {
                    assertThat(error, isA(InvalidIpException.class));
                    assertThat(error.getMessage(), is("error"));
                })
                .verify();
    }

    @Test
    public void getCountryLocation_whenIpValidationReturnsEmptyMono_shouldGetTheCountryLocationOfTheHostMachinePublicIp() {
        final IpCountryLocation ipCountryLocationDomain = createIpCountryLocationDomain();

        when(ipValidator.validateIp(null)).thenReturn(Mono.empty());
        when(ipCountryLocationService.getCountryLocationForIp(hostMachinePublicIp)).thenReturn(Mono.just(ipCountryLocationDomain));

        StepVerifier.create(ipLocationApi.getCountryLocation(null))
                .consumeSubscriptionWith(subscription -> {
                    verify(ipValidator, times(1)).validateIp(null);
                    verify(ipCountryLocationService, times(1)).getCountryLocationForIp(hostMachinePublicIp);

                    verifyNoMoreInteractions(ipValidator);
                    verifyNoMoreInteractions(ipCountryLocationService);

                    verifyNoInteractions(ipCityLocationService);
                })
                .expectNext(me.github.lparo.geolocation.controller.dto.IpCountryLocation.fromDomain(ipCountryLocationDomain))
                .verifyComplete();
    }

    @Test
    public void getCountryLocation_whenIpIsPresent_shouldGetItsTheCityLocation() {
        final IpCountryLocation ipCountryLocationDomain = createIpCountryLocationDomain();

        when(ipValidator.validateIp(VALID_IP)).thenReturn(Mono.just(VALID_IP));
        when(ipCountryLocationService.getCountryLocationForIp(VALID_IP)).thenReturn(Mono.just(ipCountryLocationDomain));

        StepVerifier.create(ipLocationApi.getCountryLocation(VALID_IP))
                .consumeSubscriptionWith(subscription -> {
                    verify(ipValidator, times(1)).validateIp(VALID_IP);
                    verify(ipCountryLocationService, times(1)).getCountryLocationForIp(VALID_IP);

                    verifyNoMoreInteractions(ipValidator);
                    verifyNoMoreInteractions(ipCountryLocationService);

                    verifyNoInteractions(ipCityLocationService);
                })
                .expectNext(me.github.lparo.geolocation.controller.dto.IpCountryLocation.fromDomain(ipCountryLocationDomain))
                .verifyComplete();
    }

    private IpCityLocation createIpCityLocationDomain() {
        return IpCityLocation.of(
                City.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt()),
                State.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt(), UUID.randomUUID().toString())
        );
    }

    private IpCountryLocation createIpCountryLocationDomain() {
        return IpCountryLocation.of(
                Country.of(
                        UUID.randomUUID().toString(),
                        ThreadLocalRandom.current().nextInt(),
                        ThreadLocalRandom.current().nextBoolean(),
                        UUID.randomUUID().toString()
                )
        );
    }
}