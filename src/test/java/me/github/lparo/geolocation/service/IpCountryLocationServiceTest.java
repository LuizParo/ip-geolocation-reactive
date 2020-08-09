package me.github.lparo.geolocation.service;

import me.github.lparo.geolocation.domain.Country;
import me.github.lparo.geolocation.domain.IpCountryLocation;
import me.github.lparo.geolocation.exception.LocationNotFoundException;
import me.github.lparo.geolocation.repository.IpCountryLocationRepository;
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
class IpCountryLocationServiceTest {
    private static final String IP = "217.138.219.200";

    @InjectMocks
    private IpCountryLocationService ipCountryLocationService;

    @Mock
    private IpCountryLocationRepository ipCountryLocationRepository;

    @Test
    void getCountryLocationForIp_whenLocationIsFoundForHostIp_shouldReturnItAsAMono() {
        final IpCountryLocation expectedIpLocation = createIpCountryLocationDomain();

        when(ipCountryLocationRepository.getCountryLocationForIp(IP)).thenReturn(Mono.just(expectedIpLocation));

        StepVerifier.create(ipCountryLocationService.getCountryLocationForIp(IP))
                .consumeSubscriptionWith(subscription -> {
                    verify(ipCountryLocationRepository, times(1)).getCountryLocationForIp(IP);
                    verifyNoMoreInteractions(ipCountryLocationRepository);
                })
                .expectNext(expectedIpLocation)
                .verifyComplete();
    }

    @Test
    void getCountryLocationForIp_whenLocationIsNotFoundForIp_shouldReturnErrorAsAMono() {
        when(ipCountryLocationRepository.getCountryLocationForIp(IP)).thenReturn(Mono.empty());

        StepVerifier.create(ipCountryLocationService.getCountryLocationForIp(IP))
                .consumeSubscriptionWith(subscription -> {
                    verify(ipCountryLocationRepository, times(1)).getCountryLocationForIp(IP);
                    verifyNoMoreInteractions(ipCountryLocationRepository);
                })
                .consumeErrorWith(error -> assertThat(error, allOf(
                        isA(LocationNotFoundException.class),
                        hasProperty("message", is("unable to find country location for IP " + IP))
                )))
                .verify();
    }

    @Test
    void getCountryLocationForIp_whenTheRepositoryReturnsAnError_shouldPropagateItToTheCaller() {
        final String expectedErrorMessage = "unknown error";

        when(ipCountryLocationRepository.getCountryLocationForIp(IP)).thenReturn(Mono.error(new RuntimeException(expectedErrorMessage)));

        StepVerifier.create(ipCountryLocationService.getCountryLocationForIp(IP))
                .consumeSubscriptionWith(subscription -> {
                    verify(ipCountryLocationRepository, times(1)).getCountryLocationForIp(IP);
                    verifyNoMoreInteractions(ipCountryLocationRepository);
                })
                .consumeErrorWith(error -> assertThat(error, allOf(
                        isA(RuntimeException.class),
                        hasProperty("message", is(expectedErrorMessage))
                )))
                .verify();
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