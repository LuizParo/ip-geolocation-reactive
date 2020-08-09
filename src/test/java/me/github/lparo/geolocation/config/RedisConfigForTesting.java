package me.github.lparo.geolocation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.testcontainers.containers.GenericContainer;

import javax.annotation.PostConstruct;

@Configuration
@Profile("integration-test")
public class RedisConfigForTesting {
    private GenericContainer<?> redisContainer;

    @PostConstruct
    private void init() {
        final GenericContainer<?> container = new GenericContainer<>("redis:6.0.6").withExposedPorts(6379);
        container.start();

        this.redisContainer = container;
    }

    @Bean
    public GenericContainer<?> createRedisContainer() {
        return this.redisContainer;
    }

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(GenericContainer<?> redisContainer) {
        return new LettuceConnectionFactory(redisContainer.getHost(), redisContainer.getFirstMappedPort());
    }

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        final var keySerializer = new StringRedisSerializer();
        final JdkSerializationRedisSerializer valueSerializer = new JdkSerializationRedisSerializer();

        return new ReactiveRedisTemplate<>(
                factory,
                RedisSerializationContext.<String, Object>newSerializationContext(keySerializer)
                        .value(valueSerializer)
                        .build()
        );
    }
}
