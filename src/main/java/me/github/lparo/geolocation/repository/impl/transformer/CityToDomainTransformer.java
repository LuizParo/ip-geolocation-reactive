package me.github.lparo.geolocation.repository.impl.transformer;

import me.github.lparo.geolocation.domain.City;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Transformer responsible for converting a {@link com.maxmind.geoip2.record.City} instance into a {@link City}
 * domain instance.
 */
@Component
public class CityToDomainTransformer implements Function<com.maxmind.geoip2.record.City, City> {

    /**
     * Converts a {@link com.maxmind.geoip2.record.City} instance into a {@link City}
     * domain instance.
     *
     * @param city the {@link com.maxmind.geoip2.record.City} instance to be converted to its domain counterpart.
     *
     * @return the converted {@link City} domain instance.
     */
    @Override
    public City apply(com.maxmind.geoip2.record.City city) {
        return City.of(city.getName(), city.getGeoNameId());
    }
}
