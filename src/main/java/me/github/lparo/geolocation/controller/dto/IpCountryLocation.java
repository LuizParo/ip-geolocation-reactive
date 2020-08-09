package me.github.lparo.geolocation.controller.dto;

import lombok.Value;

/**
 * DTO representation of {@link me.github.lparo.geolocation.domain.IpCountryLocation}.
 */
@Value
public class IpCountryLocation {
    Country country;

    /**
     * Creates a {@link IpCountryLocation} DTO representation of its {@link me.github.lparo.geolocation.domain.IpCountryLocation}
     * domain counterpart.
     *
     * @param domain the {@link me.github.lparo.geolocation.domain.IpCountryLocation} to be converted into a DTO.
     *
     * @return the DTO representation of the given {@link me.github.lparo.geolocation.domain.IpCountryLocation} domain.
     */
    public static IpCountryLocation fromDomain(me.github.lparo.geolocation.domain.IpCountryLocation domain) {
        return new IpCountryLocation(Country.fromDomain(domain.getCountry()));
    }
}
