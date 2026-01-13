package com.example.cryptotrader.agent;

public record Decision(String symbol, String type) {
  public static Decision hold(String symbol) { return new Decision(symbol, "HOLD"); }
}
