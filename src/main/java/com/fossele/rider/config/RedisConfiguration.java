package com.fossele.rider.config;


import com.fossele.rider.entity.GeoLocation;
import com.fossele.rider.service.GeoLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

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
    public RedisTemplate<String, Object> redisTemplate(){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return  redisTemplate;
    }

    @Bean
    public GeoOperations<String, String> geoOperations(RedisTemplate<String, String>  template){
        return template.opsForGeo();
    }

    private final GeoLocationService geoLocationService;

    public void run(String  ...args) throws  Exception{
        geoLocationService.add(GeoLocation.builder()
                        .latitude(3)
                        .latitude(5)
                        .vehicleName("Taxi west")
                .build());

        geoLocationService.add(GeoLocation.builder()
                .latitude(5)
                .latitude(7)
                .vehicleName("Taxi east")
                .build());

        geoLocationService.add(GeoLocation.builder()
                .latitude(1)
                .latitude(5)
                .vehicleName("Taxi north")
                .build());
    }


}
