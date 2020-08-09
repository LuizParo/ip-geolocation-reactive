package me.github.lparo.geolocation.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class CityTest {
    private final String NAME = UUID.randomUUID().toString();
    private final int GEO_NAME_ID = ThreadLocalRandom.current().nextInt();

    @Test
    void of_whenCalledWithParameters_shouldAssignThemToTheRightField() {
        final City city = City.of(NAME, GEO_NAME_ID);

        assertThat(city.getName(), is(NAME));
        assertThat(city.getGeoNameId(), is(GEO_NAME_ID));
    }
}