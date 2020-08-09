package me.github.lparo.geolocation.repository.impl.redis;

import me.github.lparo.geolocation.domain.IpCityLocation;
import me.github.lparo.geolocation.repository.IpCityLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Implementing class of {@link IpCityLocationRepository} responsible for fetching the {@link IpCityLocation} information
 * from the underlying Redis server, which is being used as a cache.
 */
@Repository("redisIpCityLocationRepository")
public class RedisIpCityLocationRepository implements IpCityLocationRepository {
    private static final String REPOSITORY_TYPE = "CITY-";

    private final ReactiveValueOperations<String, IpCityLocation> valueOperations;

    @Autowired
    public RedisIpCityLocationRepository(ReactiveRedisTemplate<String, ?> reactiveRedisTemplate) {
        @SuppressWarnings("unchecked")
        final ReactiveValueOperations<String, IpCityLocation> valueOperations =
                (ReactiveValueOperations<String, IpCityLocation>) reactiveRedisTemplate.opsForValue();

        this.valueOperations = valueOperations;
    }

    /**
     * Tries to fetch the {@link IpCityLocation} from the Redis cache and returns it as an {@link Mono}. If the
     * information is missing from Redis, then an {@link Mono#empty()} is returned instead. It uses the IP address
     * as a locator key in the cache.
     *
     * @param ip the IP address to have the {@link IpCityLocation} information fetched.
     *
     * @return the {@link IpCityLocation} information of where the IP address is located wrapped in a {@link Mono}.
     */
    @Override
    public Mono<IpCityLocation> getCityLocationForIp(String ip) {
        return valueOperations.get(REPOSITORY_TYPE + ip);
    }

    /**
     * Adds a single {@link IpCityLocation} in the cache, associating it with its origin IP address (as the locator key).
     *
     * @param ip the IP address to be used as a locator key for the incoming {@link IpCityLocation}.
     * @param ipCityLocation the {@link IpCityLocation} to be persisted in the Redis cache.
     *
     * @return the {@link IpCityLocation} that was just saved into the Redis cache wrapped in a {@link Mono}.
     */
    public Mono<IpCityLocation> addToCache(String ip, IpCityLocation ipCityLocation) {
        return valueOperations.set(REPOSITORY_TYPE + ip, ipCityLocation)
                .thenReturn(ipCityLocation);
    }
}
