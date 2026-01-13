package com.example.cryptotrader.agent;

import com.example.cryptotrader.agent.dto.MarketTick;
import com.example.cryptotrader.agent.dto.TradeIntent;
import com.example.cryptotrader.domain.TradeEventType;
import com.example.cryptotrader.domain.TradeSide;
import com.example.cryptotrader.persistence.TradeEventWriter;
import com.example.cryptotrader.redis.ReactivePubSub;
import com.example.cryptotrader.redis.Topics;
import com.example.cryptotrader.util.Json;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;

@Component
public class AnalystAgent {

  private final ReactiveRedisTemplate<String, String> redis;
  private final ReactivePubSub pubsub;
  private final Topics topics;
  private final Json json;
  private final TradeEventWriter events;
  private final ChatClient chatClient;
  private final String env;
  private final ObjectMapper mapper = new ObjectMapper();

  public AnalystAgent(
      ReactiveRedisTemplate<String, String> redis,
      ReactivePubSub pubsub,
      Topics topics,
      Json json,
      TradeEventWriter events,
      ChatClient.Builder chatClientBuilder,
      @Value("${app.env}") String env
  ) {
    this.redis = redis;
    this.pubsub = pubsub;
    this.topics = topics;
    this.json = json;
    this.events = events;
    this.chatClient = chatClientBuilder.build();
    this.env = env;
  }

  @PostConstruct
  public void start() {
    Deque<MarketTick> window = new ArrayDeque<>();

    pubsub.listen(topics.marketTicks)
        .map(msg -> json.fromJson(msg.getMessage(), MarketTick.class))
        .doOnNext(tick -> {
          window.addLast(tick);
          while (window.size() > 30) window.removeFirst();
        })
        .sample(Duration.ofSeconds(5))
        .flatMap(tick -> decide(window))
        .flatMap(intent -> {
          String payload = json.toJson(intent);
          return events.write(TradeEventType.INTENT, intent.symbol(), intent.side(), intent.amount(), intent.confidence(),
                  intent.rationale(), null, payload)
              .then(redis.convertAndSend(topics.tradeIntent, payload))
              .thenReturn(intent);
        })
        .onErrorContinue((e, o) -> {})
        .subscribe();
  }

  private Flux<TradeIntent> decide(Deque<MarketTick> window) {
    double latest = window.isEmpty() ? 0.0 : window.getLast().price();
    double pseudoRsi = latest == 0.0 ? 50.0 : (latest % 100.0); // placeholder

    String prompt = """
You are an Analyst agent for a crypto trading system.
You MUST respond with a single-line JSON object with this exact schema:
{"symbol":"BTCUSDT","side":"BUY|SELL|HOLD","amount":<number>,"confidence":<0..1>,"rationale":"<short explanation>"}

Rules:
- Prefer HOLD unless confidence >= 0.70.
- amount must be between 0 and 50 (quote currency amount).
- Never output anything except the JSON.

Inputs:
- env=%s
- symbol=BTCUSDT
- latestPrice=%f
- pseudoRSI=%f
""".formatted(env, latest, pseudoRsi);

    return Flux.defer(() -> Flux.just(chatClient.prompt().user(prompt).call().content()))
        .map(content -> {
          try {
            var node = mapper.readTree(content.trim());
            String symbol = node.get("symbol").asText("BTCUSDT");
            TradeSide side = TradeSide.valueOf(node.get("side").asText("HOLD").toUpperCase());
            double amount = node.get("amount").asDouble(0.0);
            double confidence = node.get("confidence").asDouble(0.0);
            String rationale = node.get("rationale").asText("");
            return new TradeIntent(symbol, side, amount, confidence, rationale);
          } catch (Exception e) {
            return new TradeIntent("BTCUSDT", TradeSide.HOLD, 0.0, 0.0, "Parse error, defaulting to HOLD.");
          }
        })
        .onErrorReturn(new TradeIntent("BTCUSDT", TradeSide.HOLD, 0.0, 0.0, "AI error, defaulting to HOLD."));
  }
}
