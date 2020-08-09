package me.github.lparo.geolocation.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class IpCountryLocationTest {
    private final Country COUNTRY = Country.of(
            UUID.randomUUID().toString(),
            ThreadLocalRandom.current().nextInt(),
            ThreadLocalRandom.current().nextBoolean(),
            UUID.randomUUID().toString()
    );

    @Test
    void of_whenCalledWithParameters_shouldAssignThemToTheRightField() {
        final IpCountryLocation ipCountryLocation = IpCountryLocation.of(COUNTRY);
        assertThat(ipCountryLocation.getCountry(), is(COUNTRY));
    }
}