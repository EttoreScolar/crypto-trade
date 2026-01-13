package com.example.cryptotrader.executor;

import com.example.cryptotrader.agent.dto.ApprovedTrade;
import com.example.cryptotrader.domain.TradeEventType;
import com.example.cryptotrader.persistence.TradeEventWriter;
import com.example.cryptotrader.redis.ReactivePubSub;
import com.example.cryptotrader.redis.Topics;
import com.example.cryptotrader.util.Json;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;

@Component
public class ExecutorAgent {

  private final ReactiveRedisTemplate<String, String> redis;
  private final ReactivePubSub pubsub;
  private final Topics topics;
  private final Json json;
  private final TradeEventWriter events;
  private final ChatClient chatClient;
  private final TradingTools tools;

  public ExecutorAgent(
      ReactiveRedisTemplate<String, String> redis,
      ReactivePubSub pubsub,
      Topics topics,
      Json json,
      TradeEventWriter events,
      ChatClient.Builder chatClientBuilder,
      TradingTools tools
  ) {
    this.redis = redis;
    this.pubsub = pubsub;
    this.topics = topics;
    this.json = json;
    this.events = events;
    this.chatClient = chatClientBuilder.build();
    this.tools = tools;
  }

  @PostConstruct
  public void start() {
    pubsub.listen(topics.tradeApproved)
        .map(msg -> json.fromJson(msg.getMessage(), ApprovedTrade.class))
        .flatMap(this::executeApproved)
        .onErrorContinue((e, o) -> {})
        .subscribe();
  }

  private Mono<Void> executeApproved(ApprovedTrade approved) {
    String prompt = """
You are an Executor agent. A trade was approved by risk management.
You MUST place exactly one order by calling the tool placeOrder.
Constraints:
- symbol=%s
- side=%s
- amount=%f
Do not change these values.
Then return a one-line confirmation message.
""".formatted(approved.symbol(), approved.side().name(), approved.amount());

    return Mono.fromCallable(() -> chatClient.prompt().user(prompt).tools(tools).call().content())
        .flatMap(result -> {
          String payload = json.toJson(new Executed(approved.symbol(), approved.side().name(), approved.amount(), result));
          return events.write(TradeEventType.EXECUTED, approved.symbol(), approved.side(), approved.amount(),
                  approved.confidence(), approved.rationale(), null, payload)
              .then(redis.convertAndSend(topics.tradeExecuted, payload))
              .then();
        });
  }

  private record Executed(String symbol, String side, double amount, String result) {}
}
