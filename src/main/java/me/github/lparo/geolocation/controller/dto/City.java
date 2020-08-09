package me.github.lparo.geolocation.controller.dto;

import lombok.Value;

/**
 * DTO representation of {@link me.github.lparo.geolocation.domain.City}.
 */
@Value
public class City {
    String name;
    int geoNameId;

    /**
     * Creates a {@link City} DTO representation of its {@link me.github.lparo.geolocation.domain.City}
     * domain counterpart.
     *
     * @param domain the {@link me.github.lparo.geolocation.domain.City} to be converted into a DTO.
     *
     * @return the DTO representation of the given {@link me.github.lparo.geolocation.domain.City} domain.
     */
    public static City fromDomain(me.github.lparo.geolocation.domain.City domain) {
        return new City(domain.getName(), domain.getGeoNameId());
    }
}
