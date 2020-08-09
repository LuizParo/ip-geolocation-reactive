package me.github.lparo.geolocation.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Configuration file responsible for creating Spring beans related to IP handlers.
 */
@Slf4j
@Configuration
public class HostIpConfig {

    /**
     * Creates a {@link String} inside the {@link org.springframework.context.ApplicationContext} that references the
     * external public IP associated with the service's host machine.
     *
     * @return the external public IP associated with the service's host machine.
     *
     * @throws IOException in case it's not possible to retrieve the external IP from the host machine.
     */
    @Bean
    @Qualifier("hostMachinePublicIp")
    public String getHostMachinePublicIp() throws IOException {
        final URL url = new URL("http://checkip.amazonaws.com");

        try(BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            final String publicIp = in.readLine();
            log.info("found public IP associated with the current host machine: " + publicIp);

            return publicIp;
        }
    }

    /**
     * Instantiates a {@link InetAddressValidator} as a Spring bean to be injected whenever a IP address needs to be
     * validated.
     *
     * @return an {@link InetAddressValidator} instance.
     */
    @Bean
    public InetAddressValidator createIpValidator() {
        return InetAddressValidator.getInstance();
    }
}
