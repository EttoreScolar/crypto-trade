package com.example.cryptotrader.agent;

public interface Strategy {
  Decision evaluate(AgentState state);
}
