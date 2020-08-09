package me.github.lparo.geolocation.repository.impl.hybrid;

import me.github.lparo.geolocation.domain.City;
import me.github.lparo.geolocation.domain.IpCityLocation;
import me.github.lparo.geolocation.domain.State;
import me.github.lparo.geolocation.repository.impl.geoip2.GeoIP2IpCityLocationRepository;
import me.github.lparo.geolocation.repository.impl.redis.RedisIpCityLocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HybridIpCityLocationRepositoryTest {
    private static final String IP = "217.138.219.200";

    @InjectMocks
    private HybridIpCityLocationRepository hybridIpCityLocationRepository;

    @Mock
    private RedisIpCityLocationRepository redisIpCityLocationRepository;

    @Mock
    private GeoIP2IpCityLocationRepository geoIP2IpCityLocationRepository;

    @Test
    void getCityLocationForIp_whenTheLocationIsFoundInTheCache_shouldReturnItWrappedInAMono() {
        final IpCityLocation expectedIpCityLocation = createIpCityLocation();

        when(redisIpCityLocationRepository.getCityLocationForIp(IP)).thenReturn(Mono.just(expectedIpCityLocation));

        StepVerifier.create(hybridIpCityLocationRepository.getCityLocationForIp(IP))
                .consumeSubscriptionWith(subscription -> {
                    verify(redisIpCityLocationRepository, times(1)).getCityLocationForIp(IP);

                    verifyNoMoreInteractions(redisIpCityLocationRepository);
                    verifyNoInteractions(geoIP2IpCityLocationRepository);
                })
                .expectNext(expectedIpCityLocation)
                .verifyComplete();
    }

    @Test
    void getCityLocationForIp_whenTheLocationIsNotFoundInTheCache_andIsFoundInTheGeoIP2Repository_shouldPersistTheLocationInTheCache_andReturnItWrappedInAMono() {
        final IpCityLocation expectedIpCityLocation = createIpCityLocation();

        when(redisIpCityLocationRepository.getCityLocationForIp(IP)).thenReturn(Mono.empty());
        when(geoIP2IpCityLocationRepository.getCityLocationForIp(IP)).thenReturn(Mono.just(expectedIpCityLocation));
        when(redisIpCityLocationRepository.addToCache(IP, expectedIpCityLocation)).thenReturn(Mono.just(expectedIpCityLocation));

        StepVerifier.create(hybridIpCityLocationRepository.getCityLocationForIp(IP))
                .expectNext(expectedIpCityLocation)
                .verifyComplete();

        verify(redisIpCityLocationRepository, times(1)).getCityLocationForIp(IP);
        verify(geoIP2IpCityLocationRepository, times(1)).getCityLocationForIp(IP);
        verify(redisIpCityLocationRepository, times(1)).addToCache(IP, expectedIpCityLocation);

        verifyNoMoreInteractions(redisIpCityLocationRepository);
        verifyNoMoreInteractions(geoIP2IpCityLocationRepository);
    }

    @Test
    void getCityLocationForIp_whenTheLocationIsNotFoundInTheCache_andIsNotFoundInTheGeoIP2RepositoryEither_shouldReturnMonoEmpty() {
        when(redisIpCityLocationRepository.getCityLocationForIp(IP)).thenReturn(Mono.empty());
        when(geoIP2IpCityLocationRepository.getCityLocationForIp(IP)).thenReturn(Mono.empty());

        StepVerifier.create(hybridIpCityLocationRepository.getCityLocationForIp(IP))
                .verifyComplete();

        verify(redisIpCityLocationRepository, times(1)).getCityLocationForIp(IP);
        verify(geoIP2IpCityLocationRepository, times(1)).getCityLocationForIp(IP);

        verifyNoMoreInteractions(redisIpCityLocationRepository);
        verifyNoMoreInteractions(geoIP2IpCityLocationRepository);
    }

    private IpCityLocation createIpCityLocation() {
        return IpCityLocation.of(
                City.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt()),
                State.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt(), UUID.randomUUID().toString())
        );
    }
}