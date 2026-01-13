package com.example.cryptotrader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoTraderApplication {
  public static void main(String[] args) {
    SpringApplication.run(CryptoTraderApplication.class, args);
  }
}
