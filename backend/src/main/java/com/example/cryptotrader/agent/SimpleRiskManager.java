package com.example.cryptotrader.agent;

import org.springframework.stereotype.Component;

@Component
public class SimpleRiskManager implements RiskManager {
  @Override
  public RiskVerdict validate(Decision decision, AgentState state) {
    if ("HOLD".equalsIgnoreCase(decision.type())) return RiskVerdict.allow();
    return RiskVerdict.deny("Only HOLD is enabled in this starter project. Implement risk limits before trading.");
  }
}
