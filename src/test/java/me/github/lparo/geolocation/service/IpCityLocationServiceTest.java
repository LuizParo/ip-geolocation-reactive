package me.github.lparo.geolocation.service;

import me.github.lparo.geolocation.domain.City;
import me.github.lparo.geolocation.domain.IpCityLocation;
import me.github.lparo.geolocation.domain.State;
import me.github.lparo.geolocation.exception.LocationNotFoundException;
import me.github.lparo.geolocation.repository.IpCityLocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpCityLocationServiceTest {
    private static final String IP = "217.138.219.200";

    @InjectMocks
    private IpCityLocationService ipCityLocationService;

    @Mock
    private IpCityLocationRepository ipCityLocationRepository;

    @Test
    void getCityLocationForIp_whenLocationIsFoundForHostIp_shouldReturnItAsAMono() {
        final IpCityLocation expectedIpLocation = createIpCityLocation();

        when(ipCityLocationRepository.getCityLocationForIp(IP)).thenReturn(Mono.just(expectedIpLocation));

        StepVerifier.create(ipCityLocationService.getCityLocationForIp(IP))
                .consumeSubscriptionWith(subscription -> {
                    verify(ipCityLocationRepository, times(1)).getCityLocationForIp(IP);
                    verifyNoMoreInteractions(ipCityLocationRepository);
                })
                .expectNext(expectedIpLocation)
                .verifyComplete();
    }

    @Test
    void getCityLocationForIp_whenLocationIsNotFoundForIp_shouldReturnErrorAsAMono() {
        when(ipCityLocationRepository.getCityLocationForIp(IP)).thenReturn(Mono.empty());

        StepVerifier.create(ipCityLocationService.getCityLocationForIp(IP))
                .consumeSubscriptionWith(subscription -> {
                    verify(ipCityLocationRepository, times(1)).getCityLocationForIp(IP);
                    verifyNoMoreInteractions(ipCityLocationRepository);
                })
                .consumeErrorWith(error -> assertThat(error, allOf(
                        isA(LocationNotFoundException.class),
                        hasProperty("message", is("unable to find city location for IP " + IP))
                )))
                .verify();
    }

    @Test
    void getCityLocationForIp_whenTheRepositoryReturnsAnError_shouldPropagateItToTheCaller() {
        final String expectedErrorMessage = "unknown error";

        when(ipCityLocationRepository.getCityLocationForIp(IP)).thenReturn(Mono.error(new RuntimeException(expectedErrorMessage)));

        StepVerifier.create(ipCityLocationService.getCityLocationForIp(IP))
                .consumeSubscriptionWith(subscription -> {
                    verify(ipCityLocationRepository, times(1)).getCityLocationForIp(IP);
                    verifyNoMoreInteractions(ipCityLocationRepository);
                })
                .consumeErrorWith(error -> assertThat(error, allOf(
                        isA(RuntimeException.class),
                        hasProperty("message", is(expectedErrorMessage))
                )))
                .verify();
    }

    private IpCityLocation createIpCityLocation() {
        return IpCityLocation.of(
                City.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt()),
                State.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt(), UUID.randomUUID().toString())
        );
    }
}