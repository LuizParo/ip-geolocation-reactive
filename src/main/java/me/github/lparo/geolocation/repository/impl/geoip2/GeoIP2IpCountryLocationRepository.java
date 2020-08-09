package me.github.lparo.geolocation.repository.impl.geoip2;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.github.lparo.geolocation.domain.IpCountryLocation;
import me.github.lparo.geolocation.repository.IpCountryLocationRepository;
import me.github.lparo.geolocation.repository.impl.transformer.IpCountryLocationTransformer;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

/**
 * Implementing class of {@link IpCountryLocationRepository} responsible for fetching the {@link IpCountryLocation} information
 * from the underlying GeoIP2 {@link DatabaseReader}.
 */
@Slf4j
@Repository("geoIP2IpCountryLocationRepository")
@AllArgsConstructor
public class GeoIP2IpCountryLocationRepository implements IpCountryLocationRepository {
    private final DatabaseReader databaseReader;
    private final IpCountryLocationTransformer ipCountryLocationTransformer;

    /**
     * Searches the GeoIP2 datastore to find the country information of where the IP address is located. If the location is found,
     * an {@link IpCountryLocation} instance wrapped in an {@link Mono} is returned, otherwise, an {@link Mono#empty()}
     * is returned.
     *
     * @param ip the IP address to have the {@link IpCountryLocation} information fetched.
     *
     * @return the country information of where the IP address is located wrapped in an {@link Mono<IpCountryLocation>}.
     */
    @Override
    public Mono<IpCountryLocation> getCountryLocationForIp(String ip) {
        return Mono.defer(() -> Mono.justOrEmpty(getCountryLocation(ip)))
                   .map(this.ipCountryLocationTransformer);
    }

    private Optional<CountryResponse> getCountryLocation(String ip) {
        try {
            return databaseReader.tryCountry(InetAddress.getByName(ip));
        } catch (IOException | GeoIp2Exception e) {
            log.error("unable to get country location for IP " + ip, e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
