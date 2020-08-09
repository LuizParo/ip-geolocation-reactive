package me.github.lparo.geolocation.service;

import lombok.AllArgsConstructor;
import me.github.lparo.geolocation.domain.IpCountryLocation;
import me.github.lparo.geolocation.exception.LocationNotFoundException;
import me.github.lparo.geolocation.repository.IpCountryLocationRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service responsible for concentrating the logic for the {@link IpCountryLocation} domain.
 */
@Service
@AllArgsConstructor
public class IpCountryLocationService {
    private final IpCountryLocationRepository ipCountryLocationRepository;

    /**
     * Gets the {@link IpCountryLocation} containing the country information for a given IP address. If IP address does
     * not resolve to any geolocation, then an {@link LocationNotFoundException} is thrown.
     *
     * @return the {@link Mono<IpCountryLocation>} containing the country information of given IP address.
     *
     * @throws LocationNotFoundException if the IP address does not resolve to any geolocation.
     */
    public Mono<IpCountryLocation> getCountryLocationForIp(String ip) {
        return ipCountryLocationRepository.getCountryLocationForIp(ip)
                .switchIfEmpty(Mono.error(() -> new LocationNotFoundException("unable to find country location for IP " + ip)));
    }
}
