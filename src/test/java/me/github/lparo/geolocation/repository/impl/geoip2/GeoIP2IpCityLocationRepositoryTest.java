package me.github.lparo.geolocation.repository.impl.geoip2;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import me.github.lparo.geolocation.config.GeoIP2Config;
import me.github.lparo.geolocation.domain.City;
import me.github.lparo.geolocation.domain.IpCityLocation;
import me.github.lparo.geolocation.domain.State;
import me.github.lparo.geolocation.repository.impl.transformer.CityToDomainTransformer;
import me.github.lparo.geolocation.repository.impl.transformer.IpCityLocationTransformer;
import me.github.lparo.geolocation.repository.impl.transformer.SubdivisionToDomainTransformer;
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
        CityToDomainTransformer.class,
        SubdivisionToDomainTransformer.class,
        IpCityLocationTransformer.class,
        GeoIP2IpCityLocationRepository.class
}, webEnvironment = WebEnvironment.NONE)
@DirtiesContext
@ExtendWith(SpringExtension.class)
class GeoIP2IpCityLocationRepositoryTest {
    private static final String EXISTING_IP = "217.138.219.147";
    private static final String NON_EXISTING_IP = "127.0.0.1";

    private static final String CITY_NAME = "Milan";
    private static final int CITY_GEO_NAME_ID = 3173435;

    private static final String STATE_NAME = "Milan";
    private static final int STATE_GEO_NAME_ID = 3173434;
    private static final String ISO_CODE = "MI";

    @Autowired
    private GeoIP2IpCityLocationRepository repository;

    @SpyBean
    private DatabaseReader databaseReader;

    @Test
    void getCityLocationForIp_whenALocationIsFoundForTheGivenIp_shouldReturnItAsAMono() {
        final IpCityLocation expectedIpCityLocation = createIpCityLocation();

        StepVerifier.create(repository.getCityLocationForIp(EXISTING_IP))
                .expectSubscription()
                .expectNext(expectedIpCityLocation)
                .verifyComplete();
    }

    @Test
    void getCityLocationForIp_whenALocationIsNotFoundForTheGivenIp_shouldReturnMonoEmpty() {
        StepVerifier.create(repository.getCityLocationForIp(NON_EXISTING_IP))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void getCityLocationForIp_whenAnExceptionIsThrown_shouldPropagateItAsARuntimeException() throws Exception {
        final String expectedMessage = "unknown exception";

        doThrow(new GeoIp2Exception(expectedMessage))
                .when(databaseReader)
                .tryCity(InetAddress.getByName(EXISTING_IP));

        StepVerifier.create(repository.getCityLocationForIp(EXISTING_IP))
                .expectSubscription()
                .consumeErrorWith(error -> assertThat(error, allOf(
                        isA(RuntimeException.class),
                        hasProperty("message", is(expectedMessage))
                )))
                .verify();
    }

    public IpCityLocation createIpCityLocation() {
        return IpCityLocation.of(
                City.of(CITY_NAME, CITY_GEO_NAME_ID),
                State.of(STATE_NAME, STATE_GEO_NAME_ID, ISO_CODE)
        );
    }
}