package nlp.floschne.thumbnailAnnotator.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories(basePackages = "nlp.floschne.thumbnailAnnotator.db")
public class RedisConfig {
    @Bean
    @Profile("local")
    RedisConnectionFactory connectionFactoryLocal() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost"));
    }

    @Bean
    @Profile("docker")
    RedisConnectionFactory connectionFactoryDocker() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("db"));
    }

    @Bean
    RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
}

