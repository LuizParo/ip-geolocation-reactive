package me.github.lparo.geolocation.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class IpCityLocationTest {
    private final City CITY = City.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt());
    private final State STATE = State.of(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt(), UUID.randomUUID().toString());

    @Test
    void of_whenCalledWithParameters_shouldAssignThemToTheRightField() {
        final IpCityLocation ipCityLocation = IpCityLocation.of(CITY, STATE);

        assertThat(ipCityLocation.getCity(), is(CITY));
        assertThat(ipCityLocation.getState(), is(STATE));
    }
}