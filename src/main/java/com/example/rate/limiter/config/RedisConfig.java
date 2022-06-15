package com.example.rate.limiter.config;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@Slf4j
public class RedisConfig {

  @Value("${redis.hostName}")
  private String hostName;

  @Value("${redis.pass}")
  private String password;

  @Value("${redis.port}")
  private Integer port;

  @Value("${redis.pool.maxTotal}")
  private Integer maxTotal;

  @Value("${redis.pool.minIdle}")
  private Integer minIdle;

  @Value("${redis.pool.maxIdle}")
  private Integer maxIdle;

  @Bean
  JedisConnectionFactory redisConnectionFactory() {
    val redisStandaloneConfiguration = new RedisStandaloneConfiguration(hostName, port);
    redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(maxTotal);
    poolConfig.setMinIdle(minIdle);
    poolConfig.setMaxIdle(maxIdle);
    return new JedisConnectionFactory(
        redisStandaloneConfiguration,
        JedisClientConfiguration.builder().usePooling().poolConfig(poolConfig).build());
  }

  @Bean(value = "rate-limiter")
  RedisTemplate<String, String> rateLimiterRedisTemplate() {
    final RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory());
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new StringRedisSerializer());
    template.afterPropertiesSet();
    return template;
  }
}
