package me.github.lparo.geolocation.controller.dto;

import lombok.Value;

/**
 * DTO representation of {@link me.github.lparo.geolocation.domain.IpCityLocation}.
 */
@Value
public class IpCityLocation {
    City city;
    State state;

    /**
     * Creates a {@link IpCityLocation} DTO representation of its {@link me.github.lparo.geolocation.domain.IpCityLocation}
     * domain counterpart.
     *
     * @param domain the {@link me.github.lparo.geolocation.domain.IpCityLocation} to be converted into a DTO.
     *
     * @return the DTO representation of the given {@link me.github.lparo.geolocation.domain.IpCityLocation} domain.
     */
    public static IpCityLocation fromDomain(me.github.lparo.geolocation.domain.IpCityLocation domain) {
        return new IpCityLocation(
                City.fromDomain(domain.getCity()),
                State.fromDomain(domain.getState())
        );
    }
}