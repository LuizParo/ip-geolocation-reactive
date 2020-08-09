package me.github.lparo.geolocation.controller.dto;

import lombok.Value;

/**
 * DTO representation of {@link me.github.lparo.geolocation.domain.Country}.
 */
@Value
public class Country {
    String name;
    int geoNameId;
    boolean isInEuropeanUnion;
    String isoCode;

    /**
     * Creates a {@link Country} DTO representation of its {@link me.github.lparo.geolocation.domain.Country}
     * domain counterpart.
     *
     * @param domain the {@link me.github.lparo.geolocation.domain.Country} to be converted into a DTO.
     *
     * @return the DTO representation of the given {@link me.github.lparo.geolocation.domain.Country} domain.
     */
    public static Country fromDomain(me.github.lparo.geolocation.domain.Country domain) {
        return new Country(
                domain.getName(),
                domain.getGeoNameId(),
                domain.isInEuropeanUnion(),
                domain.getIsoCode()
        );
    }
}