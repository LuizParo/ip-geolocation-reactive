package me.github.lparo.geolocation.api;

import me.github.lparo.geolocation.api.validation.IpValidator;
import me.github.lparo.geolocation.controller.dto.IpCityLocation;
import me.github.lparo.geolocation.controller.dto.IpCountryLocation;
import me.github.lparo.geolocation.service.IpCityLocationService;
import me.github.lparo.geolocation.service.IpCountryLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * API class responsible for providing a facade for the interaction between the external (DTOs) and domain layers of the
 * application. It should contain as minimal logic as possible, mostly forwarding calls to methods of other classes and services.
 */
@Component
public class IpLocationApi {
    private final IpValidator ipValidator;
    private final IpCityLocationService ipCityLocationService;
    private final IpCountryLocationService ipCountryLocationService;
    private final String hostMachinePublicIp;

    @Autowired
    public IpLocationApi(IpValidator ipValidator,
                         IpCityLocationService ipCityLocationService,
                         IpCountryLocationService ipCountryLocationService,
                         @Qualifier("hostMachinePublicIp") String hostMachinePublicIp) {
        this.ipValidator = ipValidator;
        this.ipCityLocationService = ipCityLocationService;
        this.ipCountryLocationService = ipCountryLocationService;
        this.hostMachinePublicIp = hostMachinePublicIp;
    }

    /**
     * Gets the city/state information of the given IP address. If the IP is null or an empty {@link String},
     * then the service's host machine public IP will be used instead.
     *
     * @param ip the given IP address to have its city/state location fetched. Can be null or empty.
     * @return an {@link Mono<IpCityLocation>} with the information about the location of the given IP address.
     *
     * @throws me.github.lparo.geolocation.exception.InvalidIpException if the specified IP address is not a valid IPv4 address.
     */
    public Mono<IpCityLocation> getCityLocation(String ip) {
        return ipValidator.validateIp(ip)
                          .defaultIfEmpty(hostMachinePublicIp)
                          .flatMap(ipCityLocationService::getCityLocationForIp)
                          .map(IpCityLocation::fromDomain);
    }

    /**
     * Gets the country information of the given IP address. If the IP is null or an empty {@link String},
     * then the service's host machine public IP will be used instead.
     *
     * @param ip the given IP address to have its country location fetched. Can be null or empty.
     * @return an {@link Mono<IpCountryLocation>} with the information about the location of the given IP address.
     *
     * @throws me.github.lparo.geolocation.exception.InvalidIpException if the specified IP address is not a valid IPv4 address.
     */
    public Mono<IpCountryLocation> getCountryLocation(String ip) {
        return ipValidator.validateIp(ip)
                          .defaultIfEmpty(hostMachinePublicIp)
                          .flatMap(ipCountryLocationService::getCountryLocationForIp)
                          .map(IpCountryLocation::fromDomain);
    }
}
