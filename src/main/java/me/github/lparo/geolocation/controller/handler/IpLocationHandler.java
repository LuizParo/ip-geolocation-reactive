package me.github.lparo.geolocation.controller.handler;

import lombok.AllArgsConstructor;
import me.github.lparo.geolocation.api.IpLocationApi;
import me.github.lparo.geolocation.controller.dto.IpCityLocation;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@AllArgsConstructor
public class IpLocationHandler {
    private final IpLocationApi ipLocationApi;

    public Mono<ServerResponse> getCityLocation(ServerRequest request) {
        final String ip = request.queryParam("ip").orElse("");

        return ipLocationApi.getCityLocation(ip)
                .flatMap(ipCityLocation ->
                        ServerResponse.ok()
                                .contentType(APPLICATION_JSON)
                                .body(Mono.just(ipCityLocation), IpCityLocation.class)
                );
    }

    public Mono<ServerResponse> getCountryLocation(ServerRequest request) {
        final String ip = request.queryParam("ip").orElse("");

        return ipLocationApi.getCountryLocation(ip)
                .flatMap(ipCountryLocation ->
                        ServerResponse.ok()
                                .contentType(APPLICATION_JSON)
                                .body(Mono.just(ipCountryLocation), IpCityLocation.class)
                );
    }
}
