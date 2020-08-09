package me.github.lparo.geolocation.repository.impl.redis;

import me.github.lparo.geolocation.config.RedisConfigForTesting;
import me.github.lparo.geolocation.domain.City;
import me.github.lparo.geolocation.domain.IpCityLocation;
import me.github.lparo.geolocation.domain.State;
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
        RedisIpCityLocationRepository.class
}, webEnvironment = WebEnvironment.NONE)
@DirtiesContext
@ActiveProfiles("integration-test")
@ExtendWith(SpringExtension.class)
class RedisIpCityLocationRepositoryTest {
    private static final String REPOSITORY_TYPE = "CITY-";

    private static final String CACHED_IP = "217.138.219.147";
    private static final String UNCACHED_IP = "127.0.0.1";

    private static final IpCityLocation CACHED_IP_CITY_LOCATION = createIpCityLocation();
    private static final IpCityLocation UNCACHED_IP_CITY_LOCATION = createIpCityLocation();

    @Autowired
    private RedisIpCityLocationRepository repository;

    @Autowired
    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    private ReactiveValueOperations<String, Object> valueOperations;

    @PostConstruct
    private void init() {
        this.valueOperations = reactiveRedisTemplate.opsForValue();
        this.valueOperations.set(REPOSITORY_TYPE + CACHED_IP, CACHED_IP_CITY_LOCATION).block();
    }

    @Test
    void getCityLocationForIp_whenCalledWithUncachedIp_shouldReturnMonoEmpty() {
        StepVerifier.create(repository.getCityLocationForIp(UNCACHED_IP))
                .verifyComplete();
    }

    @Test
    void getCityLocationForIp_whenCalledWithCachedIp_shouldReturnLocationWrappedInAMono() {
        StepVerifier.create(repository.getCityLocationForIp(CACHED_IP))
                .expectNext(CACHED_IP_CITY_LOCATION)
                .verifyComplete();
    }

    @Test
    public void addToCache_whenCalledWithLocation_shouldSaveItIntoTheCache() {
        StepVerifier.create(repository.addToCache(UNCACHED_IP, UNCACHED_IP_CITY_LOCATION))
                .expectNext(UNCACHED_IP_CITY_LOCATION)
                .verifyComplete();

        StepVerifier.create(valueOperations.get(REPOSITORY_TYPE + UNCACHED_IP))
                .expectNext(UNCACHED_IP_CITY_LOCATION)
                .verifyComplete();
    }

    private static IpCityLocation createIpCityLocation() {
        return IpCityLocation.of(
                City.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt()),
                State.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt(), UUID.randomUUID().toString())
        );
    }
}