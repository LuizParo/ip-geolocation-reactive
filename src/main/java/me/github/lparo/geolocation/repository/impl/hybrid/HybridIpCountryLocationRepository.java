package me.github.lparo.geolocation.repository.impl.hybrid;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.github.lparo.geolocation.domain.IpCountryLocation;
import me.github.lparo.geolocation.repository.IpCountryLocationRepository;
import me.github.lparo.geolocation.repository.impl.geoip2.GeoIP2IpCountryLocationRepository;
import me.github.lparo.geolocation.repository.impl.redis.RedisIpCountryLocationRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Implementing class of {@link IpCountryLocationRepository} responsible for fetching the {@link IpCountryLocation} information
 * from Redis cache, or the GeoIP2 repository if it's missing there.
 */
@Slf4j
@Primary
@Repository
@AllArgsConstructor
public class HybridIpCountryLocationRepository implements IpCountryLocationRepository {
    private final GeoIP2IpCountryLocationRepository geoIP2IpCountryLocationRepository;
    private final RedisIpCountryLocationRepository redisIpCountryLocationRepository;

    /**
     * Tries to fetch the {@link IpCountryLocation} from the Redis cache and return it wrapped in an {@link Mono}. It
     * uses the IP address as a locator for the cached information. If the location for the IP is missing from the cache,
     * then it's gonna try to retrieve it from the GeoIP2 repository, and if it is in there, it's then added to the cache
     * for posterior calls. In case the location is absent on both Redis and GeoIP2 repositories, then an {@link Mono#empty()}
     * is returned instead.
     *
     * @param ip the IP address to have the {@link IpCountryLocation} information fetched.
     *
     * @return the found {@link IpCountryLocation} wrapped in an {@link Mono}, or {@link Mono#empty()} if not found.
     */
    @Override
    public Mono<IpCountryLocation> getCountryLocationForIp(String ip) {
        return redisIpCountryLocationRepository
                .getCountryLocationForIp(ip)
                .doOnNext(ipCountryLocation -> log.info("found cached country location for IP " + ip))
                .switchIfEmpty(Mono.defer(() ->
                        geoIP2IpCountryLocationRepository
                                .getCountryLocationForIp(ip)
                                .doOnNext(ipCountryLocation -> log.info(String.format("getting country location for IP %s from GeoIP2 database", ip)))
                                .flatMap(ipCountryLocation -> redisIpCountryLocationRepository.addToCache(ip, ipCountryLocation))
                ));
    }
}
