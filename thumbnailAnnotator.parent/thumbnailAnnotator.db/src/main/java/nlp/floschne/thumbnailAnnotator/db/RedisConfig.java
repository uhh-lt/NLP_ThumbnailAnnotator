package nlp.floschne.thumbnailAnnotator.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nlp.floschne.thumbnailAnnotator.core.domain.SentenceContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.convert.RedisCustomConversions;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Configuration
@EnableRedisRepositories(basePackages = "nlp.floschne.thumbnailAnnotator.db")
@ComponentScan
public class RedisConfig {

    @Component
    @ReadingConverter
    public class BytesToSentenceContextConverter implements Converter<byte[], SentenceContext> {
        @Override
        public SentenceContext convert(final byte[] source) {
            ObjectMapper jsonMapper = new ObjectMapper();
            String json = new String(source);
            try {
                return jsonMapper.readValue(json, SentenceContext.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Component
    @WritingConverter
    public class SentenceContextToByteConverter implements Converter<SentenceContext, byte[]> {
        @Override
        public byte[] convert(final SentenceContext source) {
            ObjectMapper jsonMapper = new ObjectMapper();
            try {
                String json = jsonMapper.writeValueAsString(source);
                return json.getBytes();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Bean
    public RedisCustomConversions redisCustomConversions(BytesToSentenceContextConverter bytesToContext,
                                                         SentenceContextToByteConverter contextToByes) {
        return new RedisCustomConversions(Arrays.asList(bytesToContext, contextToByes));
    }

    @Bean
    @Profile("local")
    RedisConnectionFactory connectionFactoryLocal() {
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration("localhost");
        conf.setDatabase(1);
        return new LettuceConnectionFactory(conf);
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

