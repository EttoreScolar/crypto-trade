package com.example.cryptotrader.agent;

import com.example.cryptotrader.binance.BinanceClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExecutionService {
  private final BinanceClientFactory factory;
  private final String env;

  public ExecutionService(BinanceClientFactory factory, @Value("${app.crypto.env}") String env) {
    this.factory = factory;
    this.env = env;
  }

  public AgentState currentState() {
    // MVP: single user stub. Replace with real auth + per-user state.
    return new AgentState(1L, env);
  }

  public String execute(Decision decision) {
    // MVP: no real trading in starter.
    // Wire real order placement using factory.create().tradeApi().newOrder(...) when ready.
    return "{\"status\":\"noop\",\"decisionType\":\"" + decision.type() + "\"}";
  }
}
