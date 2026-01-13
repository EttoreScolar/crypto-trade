package com.example.cryptotrader.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

  @Bean
  public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
    var keySer = new StringRedisSerializer();
    var valSer = new StringRedisSerializer();
    var ctx = RedisSerializationContext.<String, String>newSerializationContext(keySer)
        .value(valSer)
        .build();
    return new ReactiveRedisTemplate<>(factory, ctx);
  }

  @Bean
  public WebClient binanceWebClient(
      @Value("${app.env}") String env,
      @Value("${app.binance.testnetBaseUrl}") String testnetBaseUrl,
      @Value("${app.binance.prodBaseUrl}") String prodBaseUrl
  ) {
    var baseUrl = "testnet".equalsIgnoreCase(env) ? testnetBaseUrl : prodBaseUrl;
    return WebClient.builder().baseUrl(baseUrl).build();
  }
}
