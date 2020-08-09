package me.github.lparo.geolocation.controller.router;

import me.github.lparo.geolocation.repository.IpCityLocationRepository;
import me.github.lparo.geolocation.repository.IpCountryLocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("integration-test")
@ExtendWith(SpringExtension.class)
class IpLocationRouterTest {
    private static final String ENDPOINT = "/geolocation/ips";
    private static final String VALID_IP = "217.138.219.147";
    private static final String INVALID_IP = "invalid";
    private static final String IP_WITHOUT_LOCATION = "127.0.0.1";

    @Autowired
    private WebTestClient testClient;

    @SpyBean
    private IpCityLocationRepository ipCityLocationRepository;

    @SpyBean
    private IpCountryLocationRepository ipCountryLocationRepository;

    @Test
    void getCityLocation_whenIpIsSpecified_shouldUseReturnItsCityLocation() {
        this.testClient.get()
                .uri(ENDPOINT + "/city?ip=" + VALID_IP)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.city.name").isEqualTo("Milan")
                .jsonPath("$.city.geoNameId").isEqualTo(3173435)
                .jsonPath("$.state.name").isEqualTo("Milan")
                .jsonPath("$.state.geoNameId").isEqualTo(3173434)
                .jsonPath("$.state.isoCode").isEqualTo("MI");
    }

    @Test
    void getCityLocation_whenIpIsNotSpecified_shouldUseCurrentHostExternalIp() {
        this.testClient.get()
                .uri(ENDPOINT + "/city")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.city.name").value(not(emptyOrNullString()))
                .jsonPath("$.city.geoNameId").value(notNullValue())
                .jsonPath("$.state.name").value(not(emptyOrNullString()))
                .jsonPath("$.state.geoNameId").value(notNullValue())
                .jsonPath("$.state.isoCode").value(not(emptyOrNullString()));
    }

    @Test
    void getCityLocation_whenIpIsSpecified_andItIsInvalid_shouldReturnBadRequestHttpStatus() {
        this.testClient.get()
                .uri(ENDPOINT + "/city?ip=" + INVALID_IP)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("invalid IP format: " + INVALID_IP);
    }

    @Test
    void getCityLocation_whenIpIsSpecified_andItIsDoesNotResolveToAnyLocation_shouldReturnNotFoundHttpStatus() {
        this.testClient.get()
                .uri(ENDPOINT + "/city?ip=" + IP_WITHOUT_LOCATION)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("unable to find city location for IP " + IP_WITHOUT_LOCATION);
    }

    @Test
    void getCityLocation_whenAnUnknownExceptionIsThrown_shouldReturnInternalServerErrorHttpStatus() {
        final String expectedMessage = "unknown exception";

        doThrow(new RuntimeException(expectedMessage)).when(ipCityLocationRepository).getCityLocationForIp(VALID_IP);

        this.testClient.get()
                .uri(ENDPOINT + "/city?ip=" + VALID_IP)
                .exchange()
                .expectStatus()
                .isEqualTo(INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo(expectedMessage);
    }

    @Test
    void getCountryLocation_whenIpIsSpecified_shouldUseReturnItsCCountryLocation() {
        this.testClient.get()
                .uri(ENDPOINT + "/country?ip=" + VALID_IP)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.country.name").isEqualTo("Italy")
                .jsonPath("$.country.geoNameId").isEqualTo(3175395)
                .jsonPath("$.country.inEuropeanUnion").isEqualTo(TRUE)
                .jsonPath("$.country.isoCode").isEqualTo("IT");
    }

    @Test
    void getCountryLocation_whenIpIsNotSpecified_shouldUseCurrentHostExternalIp() {
        this.testClient.get()
                .uri(ENDPOINT + "/country")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.country.name").value(not(emptyOrNullString()))
                .jsonPath("$.country.geoNameId").value(notNullValue())
                .jsonPath("$.country.inEuropeanUnion").value(notNullValue())
                .jsonPath("$.country.isoCode").value(not(emptyOrNullString()));
    }

    @Test
    void getCountryLocation_whenIpIsSpecified_andItIsInvalid_shouldReturnBadRequestHttpStatus() {
        this.testClient.get()
                .uri(ENDPOINT + "/country?ip=" + INVALID_IP)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("invalid IP format: " + INVALID_IP);
    }

    @Test
    void getCountryLocation_whenIpIsSpecified_andItIsDoesNotResolveToAnyLocation_shouldReturnNotFoundHttpStatus() {
        this.testClient.get()
                .uri(ENDPOINT + "/country?ip=" + IP_WITHOUT_LOCATION)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("unable to find country location for IP " + IP_WITHOUT_LOCATION);
    }

    @Test
    void getCountryLocation_whenAnUnknownExceptionIsThrown_shouldReturnInternalServerErrorHttpStatus() {
        final String expectedMessage = "unknown exception";

        doThrow(new RuntimeException(expectedMessage)).when(ipCountryLocationRepository).getCountryLocationForIp(VALID_IP);

        this.testClient.get()
                .uri(ENDPOINT + "/country?ip=" + VALID_IP)
                .exchange()
                .expectStatus()
                .isEqualTo(INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo(expectedMessage);
    }
}