package me.github.lparo.geolocation.repository.impl.geoip2;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.github.lparo.geolocation.domain.IpCityLocation;
import me.github.lparo.geolocation.repository.IpCityLocationRepository;
import me.github.lparo.geolocation.repository.impl.transformer.IpCityLocationTransformer;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

/**
 * Implementing class of {@link IpCityLocationRepository} responsible for fetching the {@link IpCityLocation} information
 * from the underlying GeoIP2 {@link DatabaseReader}.
 */
@Slf4j
@Repository
@AllArgsConstructor
public class GeoIP2IpCityLocationRepository implements IpCityLocationRepository {
    private final DatabaseReader databaseReader;
    private final IpCityLocationTransformer ipCityLocationTransformer;

    /**
     * Searches the GeoIP2 datastore to find the city/state information of where the IP address is located. If the location is found,
     * an {@link IpCityLocation} instance wrapped in an {@link Mono} is returned, otherwise, an {@link Mono#empty()}
     * is returned.
     *
     * @param ip the IP address to have the {@link IpCityLocation} information fetched.
     *
     * @return the city/state information of where the IP address is located wrapped in an {@link Mono<IpCityLocation>}.
     */
    @Override
    public Mono<IpCityLocation> getCityLocationForIp(String ip) {
            return Mono.defer(() -> Mono.justOrEmpty(getCityLocation(ip)))
                       .map(this.ipCityLocationTransformer);
    }

    private Optional<CityResponse> getCityLocation(String ip) {
        try {
            return databaseReader.tryCity(InetAddress.getByName(ip));
        } catch (IOException | GeoIp2Exception e) {
            log.error("unable to get city location for IP " + ip, e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
