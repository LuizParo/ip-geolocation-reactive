package me.github.lparo.geolocation.api.validation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.github.lparo.geolocation.exception.InvalidIpException;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Class responsible for performing validations on the incoming IP address.
 */
@Slf4j
@Component
@AllArgsConstructor
public class IpValidator {
    private final InetAddressValidator validator;

    /**
     * Validates if the specified IP address is a valid IPv4 address.
     *
     * @param ip the IP address to be validated.
     *
     * @return the {@link Mono<String>} containing the validated IP address.
     *
     * @throws InvalidIpException if the specified IP address is not a valid IPv4 address.
     */
    public Mono<String> validateIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return Mono.empty();
        }

        if (!validator.isValidInet4Address(ip)) {
            return Mono.error(() -> new InvalidIpException("invalid IP format: " + ip));
        }

        return Mono.just(ip);
    }
}