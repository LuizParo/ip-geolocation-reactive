package me.github.lparo.geolocation.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class CountryTest {
    private final String NAME = UUID.randomUUID().toString();
    private final int GEO_NAME_ID = ThreadLocalRandom.current().nextInt();
    private final boolean IS_IN_EUROPEAN_UNION = ThreadLocalRandom.current().nextBoolean();
    private final String ISO_CODE = UUID.randomUUID().toString();

    @Test
    void of_whenCalledWithParameters_shouldAssignThemToTheRightField() {
        final Country country = Country.of(NAME, GEO_NAME_ID, IS_IN_EUROPEAN_UNION, ISO_CODE);

        assertThat(country.getName(), is(NAME));
        assertThat(country.getGeoNameId(), is(GEO_NAME_ID));
        assertThat(country.isInEuropeanUnion(), is(IS_IN_EUROPEAN_UNION));
        assertThat(country.getIsoCode(), is(ISO_CODE));
    }
}