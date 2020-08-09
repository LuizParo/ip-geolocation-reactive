package me.github.lparo.geolocation.repository.impl.transformer;

import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Subdivision;
import me.github.lparo.geolocation.domain.City;
import me.github.lparo.geolocation.domain.IpCityLocation;
import me.github.lparo.geolocation.domain.State;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpCityLocationTransformerTest {
    private static final String CITY_NAME = UUID.randomUUID().toString();
    private static final int CITY_GEO_NAME_ID = ThreadLocalRandom.current().nextInt();

    private static final String STATE_NAME = UUID.randomUUID().toString();
    private static final int STATE_GEO_NAME_ID = ThreadLocalRandom.current().nextInt();
    private static final String ISO_CODE = UUID.randomUUID().toString();

    @InjectMocks
    private IpCityLocationTransformer transformer;

    @Mock
    private CityToDomainTransformer cityToDomainTransformer;

    @Mock
    private SubdivisionToDomainTransformer subdivisionToDomainTransformer;

    @Test
    void apply_whenCalledWithRecord_shouldTransformItToItsDomainCounterpart() {
        final CityResponse record = createRecord();
        final City cityDomain = City.of(CITY_NAME, CITY_GEO_NAME_ID);
        final State stateDomain = State.of(STATE_NAME, STATE_GEO_NAME_ID, ISO_CODE);

        when(cityToDomainTransformer.apply(record.getCity())).thenReturn(cityDomain);
        when(subdivisionToDomainTransformer.apply(record.getMostSpecificSubdivision())).thenReturn(stateDomain);

        final IpCityLocation domain = transformer.apply(record);

        verify(cityToDomainTransformer, times(1)).apply(record.getCity());
        verify(subdivisionToDomainTransformer, times(1)).apply(record.getMostSpecificSubdivision());

        verifyNoMoreInteractions(cityToDomainTransformer);
        verifyNoMoreInteractions(subdivisionToDomainTransformer);

        assertThat(domain, notNullValue());
        assertThat(domain.getCity(), is(cityDomain));
        assertThat(domain.getState(), is(stateDomain));
    }

    private CityResponse createRecord() {
        final com.maxmind.geoip2.record.City city = new com.maxmind.geoip2.record.City(
                singletonList(Locale.US.getLanguage()),
                ThreadLocalRandom.current().nextInt(),
                CITY_GEO_NAME_ID,
                singletonMap(Locale.US.getLanguage(), CITY_NAME)
        );

        final Subdivision subdivision = new Subdivision(
                singletonList(Locale.US.getLanguage()),
                ThreadLocalRandom.current().nextInt(),
                STATE_GEO_NAME_ID,
                ISO_CODE,
                singletonMap(Locale.US.getLanguage(), STATE_NAME)
        );

        final ArrayList<Subdivision> subdivisions = new ArrayList<>();
        subdivisions.add(subdivision);

        return new CityResponse(
                city,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                subdivisions,
                null
        );
    }
}