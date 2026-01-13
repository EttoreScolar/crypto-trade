package com.example.cryptotrader.market;

import com.example.cryptotrader.agent.dto.MarketTick;
import com.example.cryptotrader.redis.Topics;
import com.example.cryptotrader.util.Json;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;

@Component
public class MarketDataPublisher {

  private final WebClient binance;
  private final ReactiveRedisTemplate<String, String> redis;
  private final Topics topics;
  private final Json json;

  public MarketDataPublisher(WebClient binance, ReactiveRedisTemplate<String, String> redis, Topics topics, Json json) {
    this.binance = binance;
    this.redis = redis;
    this.topics = topics;
    this.json = json;
  }

  @PostConstruct
  public void start() {
    Flux.interval(Duration.ofSeconds(1))
        .flatMap(t -> fetchPrice("BTCUSDT"))
        .flatMap(tick -> redis.convertAndSend(topics.marketTicks, json.toJson(tick)).thenReturn(tick))
        .onErrorContinue((e, o) -> {})
        .subscribe();
  }

  private Flux<MarketTick> fetchPrice(String symbol) {
    return binance.get()
        .uri(uriBuilder -> uriBuilder.path("/api/v3/ticker/price").queryParam("symbol", symbol).build())
        .retrieve()
        .bodyToMono(Map.class)
        .map(m -> Double.parseDouble(String.valueOf(m.get("price"))))
        .map(price -> new MarketTick(symbol, price, System.currentTimeMillis()))
        .flux();
  }
}
