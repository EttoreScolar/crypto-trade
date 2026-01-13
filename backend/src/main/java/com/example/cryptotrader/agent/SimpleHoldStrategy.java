package com.example.cryptotrader.agent;

import org.springframework.stereotype.Component;

@Component
public class SimpleHoldStrategy implements Strategy {
  @Override
  public Decision evaluate(AgentState state) {
    return Decision.hold("BTCUSDT");
  }
}
