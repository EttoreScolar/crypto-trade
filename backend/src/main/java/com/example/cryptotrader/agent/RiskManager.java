package com.example.cryptotrader.agent;

public interface RiskManager {
  RiskVerdict validate(Decision decision, AgentState state);
}
