package com.guimu.alpha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableEurekaClient
@SpringBootApplication
public class AlphaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlphaApplication.class, args);
    }


    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName("127.0.0.1");
        factory.setPort(6378);
        factory.setPassword("");
        return factory;
    }

    @Bean(name = "redisTrafficTemplate")
    public RedisTemplate<String, String> redisTrafficTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}

