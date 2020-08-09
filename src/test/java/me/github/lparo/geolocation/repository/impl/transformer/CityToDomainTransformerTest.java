package me.github.lparo.geolocation.repository.impl.transformer;

import me.github.lparo.geolocation.domain.City;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class CityToDomainTransformerTest {
    private static final String NAME = UUID.randomUUID().toString();
    private static final int GEO_NAME_ID = ThreadLocalRandom.current().nextInt();

    private final CityToDomainTransformer transformer = new CityToDomainTransformer();

    @Test
    void apply_whenCalledWithRecord_shouldTransformItToItsDomainCounterpart() {
        final com.maxmind.geoip2.record.City record = createRecord();
        final City domain = transformer.apply(record);

        assertThat(domain, notNullValue());
        assertThat(domain.getName(), is(record.getName()));
        assertThat(domain.getGeoNameId(), is(record.getGeoNameId()));
    }

    private com.maxmind.geoip2.record.City createRecord() {
        return new com.maxmind.geoip2.record.City(
                singletonList(Locale.US.getLanguage()),
                ThreadLocalRandom.current().nextInt(),
                GEO_NAME_ID,
                singletonMap(Locale.US.getLanguage(), NAME)
        );
    }
}