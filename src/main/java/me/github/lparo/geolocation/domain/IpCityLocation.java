package me.github.lparo.geolocation.domain;

import lombok.Value;

import java.io.Serializable;

/**
 * The container domain that wraps the {@link City} and {@link State} a given IP address is located.
 *
 * @see City
 * @see State
 */
@Value(staticConstructor = "of")
public class IpCityLocation implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * the {@link City} information where the IP address is located.
     */
    City city;

    /**
     * the {@link State} information where the IP address is located.
     */
    State state;
}