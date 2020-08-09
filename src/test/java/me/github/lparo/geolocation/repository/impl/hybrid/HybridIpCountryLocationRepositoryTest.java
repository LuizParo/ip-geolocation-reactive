package me.github.lparo.geolocation.repository.impl.hybrid;

import me.github.lparo.geolocation.domain.Country;
import me.github.lparo.geolocation.domain.IpCountryLocation;
import me.github.lparo.geolocation.repository.impl.geoip2.GeoIP2IpCountryLocationRepository;
import me.github.lparo.geolocation.repository.impl.redis.RedisIpCountryLocationRepository;
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
class HybridIpCountryLocationRepositoryTest {
    private static final String IP = "217.138.219.200";

    @InjectMocks
    private HybridIpCountryLocationRepository hybridIpCountryLocationRepository;

    @Mock
    private RedisIpCountryLocationRepository redisIpCountryLocationRepository;

    @Mock
    private GeoIP2IpCountryLocationRepository geoIP2IpCountryLocationRepository;

    @Test
    void getCountryLocationForIp_whenTheLocationIsFoundInTheCache_shouldReturnItWrappedInAMono() {
        final IpCountryLocation expectedIpCountryLocation = createIpCountryLocation();

        when(redisIpCountryLocationRepository.getCountryLocationForIp(IP)).thenReturn(Mono.just(expectedIpCountryLocation));

        StepVerifier.create(hybridIpCountryLocationRepository.getCountryLocationForIp(IP))
                .consumeSubscriptionWith(subscription -> {
                    verify(redisIpCountryLocationRepository, times(1)).getCountryLocationForIp(IP);

                    verifyNoMoreInteractions(redisIpCountryLocationRepository);
                    verifyNoInteractions(geoIP2IpCountryLocationRepository);
                })
                .expectNext(expectedIpCountryLocation)
                .verifyComplete();
    }

    @Test
    void getCountryLocationForIp_whenTheLocationIsNotFoundInTheCache_andIsFoundInTheGeoIP2Repository_shouldPersistTheLocationInTheCache_andReturnItWrappedInAMono() {
        final IpCountryLocation expectedIpCountryLocation = createIpCountryLocation();

        when(redisIpCountryLocationRepository.getCountryLocationForIp(IP)).thenReturn(Mono.empty());
        when(geoIP2IpCountryLocationRepository.getCountryLocationForIp(IP)).thenReturn(Mono.just(expectedIpCountryLocation));
        when(redisIpCountryLocationRepository.addToCache(IP, expectedIpCountryLocation)).thenReturn(Mono.just(expectedIpCountryLocation));

        StepVerifier.create(hybridIpCountryLocationRepository.getCountryLocationForIp(IP))
                .expectNext(expectedIpCountryLocation)
                .verifyComplete();

        verify(redisIpCountryLocationRepository, times(1)).getCountryLocationForIp(IP);
        verify(redisIpCountryLocationRepository, times(1)).addToCache(IP, expectedIpCountryLocation);
        verify(geoIP2IpCountryLocationRepository, times(1)).getCountryLocationForIp(IP);

        verifyNoMoreInteractions(redisIpCountryLocationRepository);
        verifyNoMoreInteractions(geoIP2IpCountryLocationRepository);
    }

    @Test
    void getCountryLocationForIp_whenTheLocationIsNotFoundInTheCache_andIsNotFoundInTheGeoIP2RepositoryEither_shouldReturnMonoEmpty() {
        when(redisIpCountryLocationRepository.getCountryLocationForIp(IP)).thenReturn(Mono.empty());
        when(geoIP2IpCountryLocationRepository.getCountryLocationForIp(IP)).thenReturn(Mono.empty());

        StepVerifier.create(hybridIpCountryLocationRepository.getCountryLocationForIp(IP))
                .verifyComplete();

        verify(redisIpCountryLocationRepository, times(1)).getCountryLocationForIp(IP);
        verify(geoIP2IpCountryLocationRepository, times(1)).getCountryLocationForIp(IP);

        verifyNoMoreInteractions(redisIpCountryLocationRepository);
        verifyNoMoreInteractions(geoIP2IpCountryLocationRepository);
    }

    private IpCountryLocation createIpCountryLocation() {
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