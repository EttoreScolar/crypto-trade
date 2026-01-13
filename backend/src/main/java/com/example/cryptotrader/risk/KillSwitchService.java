package com.example.cryptotrader.risk;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class KillSwitchService {
  private final ReactiveRedisTemplate<String, String> redis;
  private final String key;
  private final boolean defaultEnabled;

  public KillSwitchService(
      ReactiveRedisTemplate<String, String> redis,
      @Value("${app.killSwitch.redisKey}") String key,
      @Value("${app.killSwitch.defaultEnabled}") boolean defaultEnabled
  ) {
    this.redis = redis;
    this.key = key;
    this.defaultEnabled = defaultEnabled;
  }

  public Mono<Boolean> isEnabled() {
    return redis.opsForValue().get(key)
        .map(v -> "true".equalsIgnoreCase(v))
        .switchIfEmpty(Mono.just(defaultEnabled));
  }

  public Mono<Boolean> setEnabled(boolean enabled) {
    return redis.opsForValue().set(key, Boolean.toString(enabled)).thenReturn(enabled);
  }
}
