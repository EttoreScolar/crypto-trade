package com.example.cryptotrader.executor;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class TradingTools {

  @Tool(description = "Places a BUY or SELL order for a specific crypto symbol. Use this only when a trade is approved by the risk agent.")
  public String placeOrder(String symbol, String side, double amount) {
    // MVP: no real trading in scaffold.
    return "NOOP order: " + side + " " + amount + " " + symbol;
  }
}
