package me.github.lparo.geolocation.repository.impl.transformer;

import me.github.lparo.geolocation.domain.State;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

class SubdivisionToDomainTransformerTest {
    private static final String NAME = UUID.randomUUID().toString();
    private static final int GEO_NAME_ID = ThreadLocalRandom.current().nextInt();
    private static final String ISO_CODE = UUID.randomUUID().toString();

    private final SubdivisionToDomainTransformer transformer = new SubdivisionToDomainTransformer();

    @Test
    void apply_whenCalledWithRecord_shouldTransformItToItsDomainCounterpart() {
        final com.maxmind.geoip2.record.Subdivision record = createRecord();
        final State domain = transformer.apply(record);

        assertThat(domain, notNullValue());
        assertThat(domain.getName(), is(record.getName()));
        assertThat(domain.getGeoNameId(), is(record.getGeoNameId()));
        assertThat(domain.getIsoCode(), is(record.getIsoCode()));
    }

    private com.maxmind.geoip2.record.Subdivision createRecord() {
        return new com.maxmind.geoip2.record.Subdivision(
                singletonList(Locale.US.getLanguage()),
                ThreadLocalRandom.current().nextInt(),
                GEO_NAME_ID,
                ISO_CODE,
                singletonMap(Locale.US.getLanguage(), NAME)
        );
    }
}