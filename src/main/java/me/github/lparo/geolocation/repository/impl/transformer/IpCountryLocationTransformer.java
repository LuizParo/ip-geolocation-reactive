package me.github.lparo.geolocation.repository.impl.transformer;

import com.maxmind.geoip2.model.CountryResponse;
import lombok.AllArgsConstructor;
import me.github.lparo.geolocation.domain.IpCountryLocation;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Transformer responsible for converting a {@link CountryResponse} instance into a {@link IpCountryLocation} domain instance.
 */
@Component
@AllArgsConstructor
public class IpCountryLocationTransformer implements Function<CountryResponse, IpCountryLocation> {
    private final CountryToDomainTransformer countryToDomainTransformer;

    /**
     * Converts a {@link CountryResponse} instance into a {@link IpCountryLocation} domain instance.
     *
     * @param cityResponse the {@link CountryResponse} instance to be converted to its domain counterpart.
     *
     * @return the converted {@link IpCountryLocation} domain instance.
     */
    @Override
    public IpCountryLocation apply(CountryResponse cityResponse) {
        return IpCountryLocation.of(countryToDomainTransformer.apply(cityResponse.getCountry()));
    }
}
