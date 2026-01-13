package com.example.cryptotrader.agent;

public record RiskVerdict(boolean allowed, String reason) {
  public static RiskVerdict allow() { return new RiskVerdict(true, null); }
  public static RiskVerdict deny(String reason) { return new RiskVerdict(false, reason); }
}
