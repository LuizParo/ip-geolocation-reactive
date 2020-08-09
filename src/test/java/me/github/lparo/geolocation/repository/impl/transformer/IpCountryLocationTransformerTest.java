package me.github.lparo.geolocation.repository.impl.transformer;

import com.maxmind.geoip2.model.CountryResponse;
import me.github.lparo.geolocation.domain.Country;
import me.github.lparo.geolocation.domain.IpCountryLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class IpCountryLocationTransformerTest {
    private static final String NAME = UUID.randomUUID().toString();
    private static final int GEO_NAME_ID = ThreadLocalRandom.current().nextInt();
    private static final boolean IS_IN_EUROPEAN_UNION = ThreadLocalRandom.current().nextBoolean();
    private static final String ISO_CODE = UUID.randomUUID().toString();

    @InjectMocks
    private IpCountryLocationTransformer transformer;

    @Mock
    private CountryToDomainTransformer countryToDomainTransformer;

    @Test
    void apply_whenCalledWithRecord_shouldTransformItToItsDomainCounterpart() {
        final CountryResponse record = createRecord();
        final Country countryDomain = Country.of(NAME, GEO_NAME_ID, IS_IN_EUROPEAN_UNION, ISO_CODE);

        when(countryToDomainTransformer.apply(record.getCountry())).thenReturn(countryDomain);

        final IpCountryLocation domain = transformer.apply(record);

        verify(countryToDomainTransformer, times(1)).apply(record.getCountry());
        verifyNoMoreInteractions(countryToDomainTransformer);

        assertThat(domain, notNullValue());
        assertThat(domain.getCountry(), is(countryDomain));
    }

    private CountryResponse createRecord() {
        final com.maxmind.geoip2.record.Country country = new com.maxmind.geoip2.record.Country(
                singletonList(Locale.US.getLanguage()),
                ThreadLocalRandom.current().nextInt(),
                GEO_NAME_ID,
                IS_IN_EUROPEAN_UNION,
                ISO_CODE,
                singletonMap(Locale.US.getLanguage(), NAME)
        );

        return new CountryResponse(
                null,
                country,
                null,
                null,
                null,
                null
        );
    }
}