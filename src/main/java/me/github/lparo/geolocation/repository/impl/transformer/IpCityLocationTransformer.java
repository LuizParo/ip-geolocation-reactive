package me.github.lparo.geolocation.repository.impl.transformer;

import com.maxmind.geoip2.model.CityResponse;
import lombok.AllArgsConstructor;
import me.github.lparo.geolocation.domain.IpCityLocation;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Transformer responsible for converting a {@link CityResponse} instance into a {@link IpCityLocation} domain instance.
 */
@Component
@AllArgsConstructor
public class IpCityLocationTransformer implements Function<CityResponse, IpCityLocation> {
    private final CityToDomainTransformer cityToDomainTransformer;
    private final SubdivisionToDomainTransformer subdivisionToDomainTransformer;

    /**
     * Converts a {@link CityResponse} instance into a {@link IpCityLocation} domain instance.
     *
     * @param cityResponse the {@link CityResponse} instance to be converted to its domain counterpart.
     *
     * @return the converted {@link IpCityLocation} domain instance.
     */
    @Override
    public IpCityLocation apply(CityResponse cityResponse) {
        return IpCityLocation.of(
                cityToDomainTransformer.apply(cityResponse.getCity()),
                subdivisionToDomainTransformer.apply(cityResponse.getMostSpecificSubdivision())
        );
    }
}
