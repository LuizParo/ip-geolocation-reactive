package me.github.lparo.geolocation.controller.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class IpCountryLocationTest {
    private final Country COUNTRY = new Country(
            UUID.randomUUID().toString(),
            ThreadLocalRandom.current().nextInt(),
            ThreadLocalRandom.current().nextBoolean(),
            UUID.randomUUID().toString()
    );

    @Test
    void newIpCountryLocation_whenCalledWithParameters_shouldAssignThemToTheRightField() {
        final IpCountryLocation ipCountryLocation = new IpCountryLocation(COUNTRY);
        assertThat(ipCountryLocation.getCountry(), is(COUNTRY));
    }

    @Test
    void fromDomain_whenCalledWithDomain_shouldCreateADtoRepresentationOfIt() {
        final me.github.lparo.geolocation.domain.IpCountryLocation domain = createIpCountryLocationDomain();
        final IpCountryLocation dto = IpCountryLocation.fromDomain(domain);

        assertThat(dto, notNullValue());
        assertThat(dto.getCountry(), is(Country.fromDomain(domain.getCountry())));
    }

    private me.github.lparo.geolocation.domain.IpCountryLocation createIpCountryLocationDomain() {
        return me.github.lparo.geolocation.domain.IpCountryLocation.of(
                me.github.lparo.geolocation.domain.Country.of(
                        COUNTRY.getName(),
                        COUNTRY.getGeoNameId(),
                        COUNTRY.isInEuropeanUnion(),
                    COUNTRY.getIsoCode()
            )
        );
    }
}