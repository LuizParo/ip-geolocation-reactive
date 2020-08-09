package me.github.lparo.geolocation.controller.router;

import me.github.lparo.geolocation.controller.handler.IpLocationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class IpLocationRouter {
    private static final String ENDPOINT = "geolocation/ips";

    @Bean
    public RouterFunction<ServerResponse> ipLocationRoutes(IpLocationHandler handler) {
        return RouterFunctions.route(
                GET(ENDPOINT + "/city"),
                handler::getCityLocation
        ).andRoute(
                GET(ENDPOINT + "/country"),
                handler::getCountryLocation
        );
    }
}
