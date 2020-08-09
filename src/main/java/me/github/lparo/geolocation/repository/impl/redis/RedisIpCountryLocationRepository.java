package me.github.lparo.geolocation.repository.impl.redis;

import me.github.lparo.geolocation.domain.IpCountryLocation;
import me.github.lparo.geolocation.repository.IpCountryLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Implementing class of {@link IpCountryLocationRepository} responsible for fetching the {@link IpCountryLocation} information
 * from the underlying Redis server, which is being used as a cache.
 */
@Repository("redisIpCountryLocationRepository")
public class RedisIpCountryLocationRepository implements IpCountryLocationRepository {
    private static final String REPOSITORY_TYPE = "COUNTRY-";

    private final ReactiveValueOperations<String, IpCountryLocation> valueOperations;

    @Autowired
    public RedisIpCountryLocationRepository(ReactiveRedisTemplate<String, ?> reactiveRedisTemplate) {
        @SuppressWarnings("unchecked")
        final ReactiveValueOperations<String, IpCountryLocation> valueOperations =
                (ReactiveValueOperations<String, IpCountryLocation>) reactiveRedisTemplate.opsForValue();

        this.valueOperations = valueOperations;
    }

    /**
     * Tries to fetch the {@link IpCountryLocation} from the Redis cache and returns it as an {@link Mono}. If the
     * information is missing from Redis, then an {@link Mono#empty()} is returned instead. It uses the IP address
     * as a locator key in the cache.
     *
     * @param ip the IP address to have the {@link IpCountryLocation} information fetched.
     *
     * @return the {@link IpCountryLocation} information of where the IP address is located wrapped in a {@link Mono}.
     */
    @Override
    public Mono<IpCountryLocation> getCountryLocationForIp(String ip) {
        return valueOperations.get(REPOSITORY_TYPE + ip);
    }

    /**
     * Adds a single {@link IpCountryLocation} in the cache, associating it with its origin IP address (as the locator key).
     *
     * @param ip the IP address to be used as a locator key for the incoming {@link IpCountryLocation}.
     * @param ipCountryLocation the {@link IpCountryLocation} to be persisted in the Redis cache.
     *
     * @return the {@link IpCountryLocation} that was just saved into the Redis cache wrapped in a {@link Mono}.
     */
    public Mono<IpCountryLocation> addToCache(String ip, IpCountryLocation ipCountryLocation) {
        return valueOperations.set(REPOSITORY_TYPE + ip, ipCountryLocation)
                .thenReturn(ipCountryLocation);
    }
}
