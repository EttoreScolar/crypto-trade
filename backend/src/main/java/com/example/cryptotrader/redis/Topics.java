package com.example.cryptotrader.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Topics {
  public final String marketTicks;
  public final String tradeIntent;
  public final String tradeApproved;
  public final String tradeRejected;
  public final String tradeExecuted;

  public Topics(
      @Value("${app.topics.marketTicks}") String marketTicks,
      @Value("${app.topics.tradeIntent}") String tradeIntent,
      @Value("${app.topics.tradeApproved}") String tradeApproved,
      @Value("${app.topics.tradeRejected}") String tradeRejected,
      @Value("${app.topics.tradeExecuted}") String tradeExecuted
  ) {
    this.marketTicks = marketTicks;
    this.tradeIntent = tradeIntent;
    this.tradeApproved = tradeApproved;
    this.tradeRejected = tradeRejected;
    this.tradeExecuted = tradeExecuted;
  }
}
