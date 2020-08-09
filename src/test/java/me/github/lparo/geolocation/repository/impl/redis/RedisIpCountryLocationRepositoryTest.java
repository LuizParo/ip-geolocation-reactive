package me.github.lparo.geolocation.repository.impl.redis;

import me.github.lparo.geolocation.config.RedisConfigForTesting;
import me.github.lparo.geolocation.domain.Country;
import me.github.lparo.geolocation.domain.IpCountryLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(classes = {
        RedisConfigForTesting.class,
        RedisIpCountryLocationRepository.class
}, webEnvironment = WebEnvironment.NONE)
@DirtiesContext
@ActiveProfiles("integration-test")
@ExtendWith(SpringExtension.class)
class RedisIpCountryLocationRepositoryTest {
    private static final String REPOSITORY_TYPE = "COUNTRY-";

    private static final String CACHED_IP = "217.138.219.147";
    private static final String UNCACHED_IP = "127.0.0.1";

    private static final IpCountryLocation CACHED_IP_COUNTRY_LOCATION = createIpCountryLocation();
    private static final IpCountryLocation UNCACHED_IP_COUNTRY_LOCATION = createIpCountryLocation();

    @Autowired
    private RedisIpCountryLocationRepository repository;

    @Autowired
    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    private ReactiveValueOperations<String, Object> valueOperations;

    @PostConstruct
    private void init() {
        this.valueOperations = reactiveRedisTemplate.opsForValue();
        this.valueOperations.set(REPOSITORY_TYPE + CACHED_IP, CACHED_IP_COUNTRY_LOCATION).block();
    }

    @Test
    void getCountryLocationForIp_whenCalledWithUncachedIp_shouldReturnMonoEmpty() {
        StepVerifier.create(repository.getCountryLocationForIp(UNCACHED_IP))
                .verifyComplete();
    }

    @Test
    void getCountryLocationForIp_whenCalledWithCachedIp_shouldReturnLocationWrappedInAMono() {
        StepVerifier.create(repository.getCountryLocationForIp(CACHED_IP))
                .expectNext(CACHED_IP_COUNTRY_LOCATION)
                .verifyComplete();
    }

    @Test
    public void addToCache_whenCalledWithLocation_shouldSaveItIntoTheCache() {
        StepVerifier.create(repository.addToCache(UNCACHED_IP, UNCACHED_IP_COUNTRY_LOCATION))
                .expectNext(UNCACHED_IP_COUNTRY_LOCATION)
                .verifyComplete();

        StepVerifier.create(valueOperations.get(REPOSITORY_TYPE + UNCACHED_IP))
                .expectNext(UNCACHED_IP_COUNTRY_LOCATION)
                .verifyComplete();
    }

    private static IpCountryLocation createIpCountryLocation() {
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