package me.github.lparo.geolocation.config;

import com.maxmind.geoip2.DatabaseReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Configuration file responsible for creating Spring beans related to GeoIP2 library.
 */
@Configuration
public class GeoIP2Config {
    private final Resource databaseFile;

    public GeoIP2Config(@Value("classpath:data/GeoLite2-City.mmdb") Resource databaseFile) {
        this.databaseFile = databaseFile;
    }

    /**
     * Creates a {@link DatabaseReader} as a Spring bean that uses the GeoIP2 binary file as a data source.
     *
     * @return a {@link DatabaseReader} instance.
     *
     * @throws IOException in case the GeoIP2 binary file cannot be read.
     */
    @Bean
    public DatabaseReader createDatabaseReader() throws IOException {
        return new DatabaseReader.Builder(this.databaseFile.getInputStream()).build();
    }
}
