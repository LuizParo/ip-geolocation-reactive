package me.github.lparo.geolocation.repository.impl.hybrid;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.github.lparo.geolocation.domain.IpCityLocation;
import me.github.lparo.geolocation.repository.IpCityLocationRepository;
import me.github.lparo.geolocation.repository.impl.geoip2.GeoIP2IpCityLocationRepository;
import me.github.lparo.geolocation.repository.impl.redis.RedisIpCityLocationRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Implementing class of {@link IpCityLocationRepository} responsible for fetching the {@link IpCityLocation} information
 * from Redis cache, or the GeoIP2 repository if it's missing there.
 */
@Slf4j
@Primary
@Repository
@AllArgsConstructor
public class HybridIpCityLocationRepository implements IpCityLocationRepository {
    private final RedisIpCityLocationRepository redisIpCityLocationRepository;
    private final GeoIP2IpCityLocationRepository geoIP2IpCityLocationRepository;

    /**
     * Tries to fetch the {@link IpCityLocation} from the Redis cache and return it wrapped in an {@link Mono}. It
     * uses the IP address as a locator for the cached information. If the location for the IP is missing from the cache,
     * then it's gonna try to retrieve it from the GeoIP2 repository, and if it is in there, it's then added to the cache
     * for posterior calls. In case the location is absent on both Redis and GeoIP2 repositories, then an {@link Mono#empty()}
     * is returned instead.
     *
     * @param ip the IP address to have the {@link IpCityLocation} information fetched.
     *
     * @return the found {@link IpCityLocation} wrapped in an {@link Mono}, or {@link Mono#empty()} if not found.
     */
    @Override
    public Mono<IpCityLocation> getCityLocationForIp(String ip) {
        return redisIpCityLocationRepository
                .getCityLocationForIp(ip)
                .doOnNext(ipCountryLocation -> log.info("found cached city location for IP " + ip))
                .switchIfEmpty(Mono.defer(() ->
                        geoIP2IpCityLocationRepository
                                .getCityLocationForIp(ip)
                                .doOnNext(ipCountryLocation -> log.info(String.format("getting city location for IP %s from GeoIP2 database", ip)))
                                .flatMap(ipCityLocation -> redisIpCityLocationRepository.addToCache(ip, ipCityLocation))
                ));
    }
}
