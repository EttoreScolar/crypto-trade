package com.example.cryptotrader.binance;

import com.binance.connector.client.SpotRestApi;
import com.binance.connector.client.utils.ClientConfiguration;
import com.binance.connector.client.utils.SignatureConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BinanceClientFactory {

  private final String env;
  private final String apiKey;
  private final String apiSecret;
  private final String overrideBaseUrl;

  public BinanceClientFactory(
      @Value("${app.crypto.env}") String env,
      @Value("${app.crypto.binance.apiKey}") String apiKey,
      @Value("${app.crypto.binance.apiSecret}") String apiSecret,
      @Value("${app.crypto.binance.baseUrl:}") String overrideBaseUrl
  ) {
    this.env = env;
    this.apiKey = apiKey;
    this.apiSecret = apiSecret;
    this.overrideBaseUrl = overrideBaseUrl;
  }

  public SpotRestApi create() {
    SignatureConfiguration sig = new SignatureConfiguration();
    sig.setApiKey(apiKey);
    sig.setSecretKey(apiSecret);

    ClientConfiguration cfg = new ClientConfiguration();
    cfg.setSignatureConfiguration(sig);

    String baseUrl = resolveBaseUrl();
    if (baseUrl != null && !baseUrl.isBlank()) {
      cfg.setBaseUrl(baseUrl);
    }

    return new SpotRestApi(cfg);
  }

  private String resolveBaseUrl() {
    if (overrideBaseUrl != null && !overrideBaseUrl.isBlank()) return overrideBaseUrl;
    if ("testnet".equalsIgnoreCase(env)) return "https://testnet.binance.vision";
    return "https://api.binance.com";
  }
}
