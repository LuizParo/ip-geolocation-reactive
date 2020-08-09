package me.github.lparo.geolocation.domain;

import lombok.Value;

import java.io.Serializable;

/**
 * Entity that contains information about the city a given IP address is located.
 */
@Value(staticConstructor = "of")
public class City implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * tha name of the city.
     */
    String name;

    /**
     * the geo name id of the city.
     */
    int geoNameId;
}
