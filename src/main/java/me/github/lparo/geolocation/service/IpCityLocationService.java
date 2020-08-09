package me.github.lparo.geolocation.service;

import lombok.AllArgsConstructor;
import me.github.lparo.geolocation.domain.IpCityLocation;
import me.github.lparo.geolocation.exception.LocationNotFoundException;
import me.github.lparo.geolocation.repository.IpCityLocationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service responsible for concentrating the logic for the {@link IpCityLocation} domain.
 */
@Service
@AllArgsConstructor
public class IpCityLocationService {
    private final IpCityLocationRepository ipCityLocationRepository;

    /**
     * Gets the {@link Mono<IpCityLocation>} containing the city/state information for a given IP address. If IP address does
     * not resolve to any geolocation, then an {@link LocationNotFoundException} is thrown.
     *
     * @return the {@link Mono<IpCityLocation>} containing the city/state information of given IP address.
     *
     * @throws LocationNotFoundException if the IP address does not resolve to any geolocation.
     */
    public Mono<IpCityLocation> getCityLocationForIp(String ip) {
        return ipCityLocationRepository.getCityLocationForIp(ip)
                .switchIfEmpty(Mono.error(() -> new LocationNotFoundException("unable to find city location for IP " + ip)));
    }
}
