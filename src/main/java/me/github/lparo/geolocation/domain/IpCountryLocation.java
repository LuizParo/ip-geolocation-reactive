package me.github.lparo.geolocation.domain;

import lombok.Value;

import java.io.Serializable;

/**
 * The container domain that wraps the {@link Country} a given IP address is located.
 *
 * @see Country
 */
@Value(staticConstructor = "of")
public class IpCountryLocation implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * the {@link Country} information where the IP address is located.
     */
    Country country;
}
