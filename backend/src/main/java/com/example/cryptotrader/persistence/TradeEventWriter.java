package com.example.cryptotrader.persistence;

import com.example.cryptotrader.domain.*;
import com.example.cryptotrader.repo.TradeEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class TradeEventWriter {

  private final TradeEventRepository repo;
  private final String env;

  public TradeEventWriter(TradeEventRepository repo, @Value("${app.env}") String env) {
    this.repo = repo;
    this.env = env;
  }

  public Mono<TradeEvent> write(TradeEventType type, String symbol, TradeSide side, Double amount, Double confidence,
                               String rationale, String reason, String payloadJson) {
    return Mono.fromCallable(() -> {
      TradeEvent e = new TradeEvent();
      e.setEnv(env);
      e.setEventType(type);
      e.setSymbol(symbol);
      e.setSide(side);
      e.setAmount(amount);
      e.setConfidence(confidence);
      e.setRationale(rationale);
      e.setReason(reason);
      e.setPayload(payloadJson);
      return repo.save(e);
    }).subscribeOn(Schedulers.boundedElastic());
  }
}
