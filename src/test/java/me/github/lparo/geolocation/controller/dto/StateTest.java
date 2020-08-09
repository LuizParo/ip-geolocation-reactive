package me.github.lparo.geolocation.controller.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class StateTest {
    private final String NAME = UUID.randomUUID().toString();
    private final int GEO_NAME_ID = ThreadLocalRandom.current().nextInt();
    private final String ISO_CODE = UUID.randomUUID().toString();

    @Test
    void newState_whenCalledWithParameters_shouldAssignThemToTheRightField() {
        final State state = new State(NAME, GEO_NAME_ID, ISO_CODE);

        assertThat(state.getName(), is(NAME));
        assertThat(state.getGeoNameId(), is(GEO_NAME_ID));
        assertThat(state.getIsoCode(), is(ISO_CODE));
    }

    @Test
    void fromDomain_whenCalledWithDomain_shouldCreateADtoRepresentationOfIt() {
        final me.github.lparo.geolocation.domain.State domain = createStateDomain();
        final State dto = State.fromDomain(domain);

        assertThat(dto, notNullValue());
        assertThat(dto.getName(), is(domain.getName()));
        assertThat(dto.getGeoNameId(), is(domain.getGeoNameId()));
        assertThat(dto.getIsoCode(), is(domain.getIsoCode()));
    }

    private me.github.lparo.geolocation.domain.State createStateDomain() {
        return me.github.lparo.geolocation.domain.State.of(NAME, GEO_NAME_ID, ISO_CODE);
    }
}