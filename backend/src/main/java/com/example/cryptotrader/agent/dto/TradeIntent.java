package com.example.cryptotrader.agent.dto;

import com.example.cryptotrader.domain.TradeSide;

public record TradeIntent(
    String symbol,
    TradeSide side,
    double amount,
    double confidence,
    String rationale
) {}
