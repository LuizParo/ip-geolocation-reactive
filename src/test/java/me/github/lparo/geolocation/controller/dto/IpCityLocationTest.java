package me.github.lparo.geolocation.controller.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class IpCityLocationTest {
    private final City CITY = new City(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt());
    private final State STATE = new State(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt(), UUID.randomUUID().toString());

    @Test
    void newIpCityLocation_whenCalledWithParameters_shouldAssignThemToTheRightField() {
        final IpCityLocation ipCityLocation = new IpCityLocation(CITY, STATE);

        assertThat(ipCityLocation.getCity(), is(CITY));
        assertThat(ipCityLocation.getState(), is(STATE));
    }

    @Test
    void fromDomain_whenCalledWithDomain_shouldCreateADtoRepresentationOfIt() {
        final me.github.lparo.geolocation.domain.IpCityLocation domain = createIpCityLocationDomain();
        final IpCityLocation dto = IpCityLocation.fromDomain(domain);

        assertThat(dto, notNullValue());
        assertThat(dto.getCity(), is(City.fromDomain(domain.getCity())));
        assertThat(dto.getState(), is(State.fromDomain(domain.getState())));
    }

    private me.github.lparo.geolocation.domain.IpCityLocation createIpCityLocationDomain() {
        return me.github.lparo.geolocation.domain.IpCityLocation.of(
                me.github.lparo.geolocation.domain.City.of(CITY.getName(), CITY.getGeoNameId()),
                me.github.lparo.geolocation.domain.State.of(STATE.getName(), STATE.getGeoNameId(), STATE.getIsoCode())
        );
    }
}