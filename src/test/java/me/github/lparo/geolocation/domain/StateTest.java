package me.github.lparo.geolocation.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class StateTest {
    private final String NAME = UUID.randomUUID().toString();
    private final int GEO_NAME_ID = ThreadLocalRandom.current().nextInt();
    private final String ISO_CODE = UUID.randomUUID().toString();

    @Test
    void of_whenCalledWithParameters_shouldAssignThemToTheRightField() {
        final State state = State.of(NAME, GEO_NAME_ID, ISO_CODE);

        assertThat(state.getName(), is(NAME));
        assertThat(state.getGeoNameId(), is(GEO_NAME_ID));
        assertThat(state.getIsoCode(), is(ISO_CODE));
    }
}