package com.example.cryptotrader.agent.dto;

public record MarketTick(String symbol, double price, long tsEpochMillis) {}
