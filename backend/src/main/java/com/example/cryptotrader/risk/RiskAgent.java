package com.example.cryptotrader.risk;

import com.example.cryptotrader.agent.dto.ApprovedTrade;
import com.example.cryptotrader.agent.dto.TradeIntent;
import com.example.cryptotrader.domain.TradeEventType;
import com.example.cryptotrader.persistence.TradeEventWriter;
import com.example.cryptotrader.redis.ReactivePubSub;
import com.example.cryptotrader.redis.Topics;
import com.example.cryptotrader.util.Json;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;

@Component
public class RiskAgent {

  private final ReactiveRedisTemplate<String, String> redis;
  private final ReactivePubSub pubsub;
  private final Topics topics;
  private final Json json;
  private final TradeEventWriter events;
  private final KillSwitchService killSwitch;

  private final double maxAmount = 25.0;
  private final double minConfidence = 0.70;

  public RiskAgent(ReactiveRedisTemplate<String, String> redis,
                   ReactivePubSub pubsub,
                   Topics topics,
                   Json json,
                   TradeEventWriter events,
                   KillSwitchService killSwitch) {
    this.redis = redis;
    this.pubsub = pubsub;
    this.topics = topics;
    this.json = json;
    this.events = events;
    this.killSwitch = killSwitch;
  }

  @PostConstruct
  public void start() {
    pubsub.listen(topics.tradeIntent)
        .map(msg -> json.fromJson(msg.getMessage(), TradeIntent.class))
        .flatMap(this::validate)
        .onErrorContinue((e, o) -> {})
        .subscribe();
  }

  private Mono<Void> validate(TradeIntent intent) {
    return killSwitch.isEnabled().flatMap(enabled -> {
      if (enabled) return reject(intent, "Kill switch is ON.");
      if (intent.confidence() < minConfidence) return reject(intent, "Confidence below threshold: " + intent.confidence());
      if (intent.amount() <= 0 || intent.amount() > maxAmount) return reject(intent, "Amount out of bounds: " + intent.amount());
      if ("HOLD".equalsIgnoreCase(intent.side().name())) return reject(intent, "HOLD intents are not executed.");

      ApprovedTrade approved = new ApprovedTrade(intent.symbol(), intent.side(), intent.amount(), intent.confidence(), intent.rationale());
      String payload = json.toJson(approved);

      return events.write(TradeEventType.APPROVED, intent.symbol(), intent.side(), intent.amount(), intent.confidence(),
              intent.rationale(), null, payload)
          .then(redis.convertAndSend(topics.tradeApproved, payload))
          .then();
    });
  }

  private Mono<Void> reject(TradeIntent intent, String reason) {
    String payload = json.toJson(intent);
    return events.write(TradeEventType.REJECTED, intent.symbol(), intent.side(), intent.amount(), intent.confidence(),
            intent.rationale(), reason, payload)
        .then(redis.convertAndSend(topics.tradeRejected, json.toJson(new Rejected(intent.symbol(), intent.side().name(), reason))))
        .then();
  }

  private record Rejected(String symbol, String side, String reason) {}
}
