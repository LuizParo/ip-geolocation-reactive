package me.github.lparo.geolocation.controller.dto;

import lombok.Value;

/**
 * DTO representation of {@link me.github.lparo.geolocation.domain.State}.
 */
@Value
public class State {
    String name;
    int geoNameId;
    String isoCode;

    /**
     * Creates a {@link State} DTO representation of its {@link me.github.lparo.geolocation.domain.State}
     * domain counterpart.
     *
     * @param domain the {@link me.github.lparo.geolocation.domain.State} to be converted into a DTO.
     *
     * @return the DTO representation of the given {@link me.github.lparo.geolocation.domain.State} domain.
     */
    public static State fromDomain(me.github.lparo.geolocation.domain.State domain) {
        return new State(
                domain.getName(),
                domain.getGeoNameId(),
                domain.getIsoCode()
        );
    }
}
