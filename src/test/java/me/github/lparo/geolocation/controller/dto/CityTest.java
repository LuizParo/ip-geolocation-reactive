package me.github.lparo.geolocation.controller.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class CityTest {
    private final String NAME = UUID.randomUUID().toString();
    private final int GEO_NAME_ID = ThreadLocalRandom.current().nextInt();

    @Test
    void newCity_whenCalledWithParameters_shouldAssignThemToTheRightField() {
        final City city = new City(NAME, GEO_NAME_ID);

        assertThat(city.getName(), is(NAME));
        assertThat(city.getGeoNameId(), is(GEO_NAME_ID));
    }

    @Test
    void fromDomain_whenCalledWithDomain_shouldCreateADtoRepresentationOfIt() {
        final me.github.lparo.geolocation.domain.City domain = createCityDomain();
        final City dto = City.fromDomain(domain);

        assertThat(dto, notNullValue());
        assertThat(dto.getName(), is(domain.getName()));
        assertThat(dto.getGeoNameId(), is(domain.getGeoNameId()));
    }

    private me.github.lparo.geolocation.domain.City createCityDomain() {
        return me.github.lparo.geolocation.domain.City.of(NAME, GEO_NAME_ID);
    }
}