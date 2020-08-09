package me.github.lparo.geolocation.repository.impl.geoip2;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import me.github.lparo.geolocation.config.GeoIP2Config;
import me.github.lparo.geolocation.domain.Country;
import me.github.lparo.geolocation.domain.IpCountryLocation;
import me.github.lparo.geolocation.repository.impl.transformer.CountryToDomainTransformer;
import me.github.lparo.geolocation.repository.impl.transformer.IpCountryLocationTransformer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.net.InetAddress;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(classes = {
        GeoIP2Config.class,
        CountryToDomainTransformer.class,
        IpCountryLocationTransformer.class,
        GeoIP2IpCountryLocationRepository.class
}, webEnvironment = WebEnvironment.NONE)
@DirtiesContext
@ExtendWith(SpringExtension.class)
class GeoIP2IpCountryLocationRepositoryTest {
    private static final String EXISTING_IP = "217.138.219.147";
    private static final String NON_EXISTING_IP = "127.0.0.1";

    private static final String NAME = "Italy";
    private static final int GEO_NAME_ID = 3175395;
    private static final boolean IS_IN_EUROPEAN_UNION = true;
    private static final String ISO_CODE = "IT";

    @Autowired
    private GeoIP2IpCountryLocationRepository repository;

    @SpyBean
    private DatabaseReader databaseReader;

    @Test
    void getCountryLocationForIp_whenALocationIsFoundForTheGivenIp_shouldReturnItAsAMono() {
        final IpCountryLocation expectedIpCountryLocation = createIpCountryLocation();

        StepVerifier.create(repository.getCountryLocationForIp(EXISTING_IP))
                .expectSubscription()
                .expectNext(expectedIpCountryLocation)
                .verifyComplete();
    }

    @Test
    void getCountryLocationForIp_whenALocationIsNotFoundForTheGivenIp_shouldReturnMonoEmpty() {
        StepVerifier.create(repository.getCountryLocationForIp(NON_EXISTING_IP))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void getCountryLocationForIp_whenAnExceptionIsThrown_shouldPropagateItAsARuntimeException() throws Exception {
        final String expectedMessage = "unknown exception";

        doThrow(new GeoIp2Exception(expectedMessage))
                .when(databaseReader)
                .tryCountry(InetAddress.getByName(EXISTING_IP));

        StepVerifier.create(repository.getCountryLocationForIp(EXISTING_IP))
                .expectSubscription()
                .consumeErrorWith(error -> assertThat(error, allOf(
                        isA(RuntimeException.class),
                        hasProperty("message", is(expectedMessage))
                )))
                .verify();
    }

    public IpCountryLocation createIpCountryLocation() {
        return IpCountryLocation.of(Country.of(NAME, GEO_NAME_ID, IS_IN_EUROPEAN_UNION, ISO_CODE));
    }
}