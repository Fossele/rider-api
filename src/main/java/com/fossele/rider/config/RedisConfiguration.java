package com.fossele.rider.config;


import com.fossele.rider.entity.GeoLocation;
import com.fossele.rider.service.GeoLocationService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;


@Configuration
@RequiredArgsConstructor
public class RedisConfiguration implements CommandLineRunner {

    @Value("${redis.host}")
    private String redisHost;


    @Value("${redis.port}")
    private int redisPort;


    @Bean
    public JedisConnectionFactory jedisConnectionFactory(){
        JedisConnectionFactory jedisConFactory = new JedisConnectionFactory();
        jedisConFactory.setHostName(redisHost);
        jedisConFactory.setPort(redisPort);
        return jedisConFactory;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(){
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return  redisTemplate;
    }

    @Bean
    public GeoOperations<String, String> geoOperations(RedisTemplate<String, String>  template){
        return template.opsForGeo();
    }

    private final GeoLocationService geoLocationService;


    @Override
    public void run(String @NonNull ... args) throws  Exception{
        geoLocationService.add(GeoLocation.builder()
                        .latitude(3.4322)
                        .latitude(5.3344)
                        .vehicleName("Taxi west")
                .build());

        geoLocationService.add(GeoLocation.builder()
                .latitude(5.2499)
                .latitude(7.9342)
                .vehicleName("Taxi east")
                .build());

        geoLocationService.add(GeoLocation.builder()
                .latitude(1.47749)
                .latitude(5.48282)
                .vehicleName("Taxi north")
                .build());
    }


}
