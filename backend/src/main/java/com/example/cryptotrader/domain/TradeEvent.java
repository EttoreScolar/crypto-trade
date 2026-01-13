package com.example.cryptotrader.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "trade_event")
public class TradeEvent {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(nullable = false)
  private String env; // testnet/prod

  @Column(nullable = false)
  private String symbol;

  @Column(nullable = false)
  private String decision;

  @Column(name = "risk_allowed", nullable = false)
  private boolean riskAllowed;

  @Column
  private String reason;

  @Column(name = "raw_request", columnDefinition = "jsonb")
  private String rawRequest;

  @Column(name = "raw_response", columnDefinition = "jsonb")
  private String rawResponse;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }

  public String getEnv() { return env; }
  public void setEnv(String env) { this.env = env; }

  public String getSymbol() { return symbol; }
  public void setSymbol(String symbol) { this.symbol = symbol; }

  public String getDecision() { return decision; }
  public void setDecision(String decision) { this.decision = decision; }

  public boolean isRiskAllowed() { return riskAllowed; }
  public void setRiskAllowed(boolean riskAllowed) { this.riskAllowed = riskAllowed; }

  public String getReason() { return reason; }
  public void setReason(String reason) { this.reason = reason; }

  public String getRawRequest() { return rawRequest; }
  public void setRawRequest(String rawRequest) { this.rawRequest = rawRequest; }

  public String getRawResponse() { return rawResponse; }
  public void setRawResponse(String rawResponse) { this.rawResponse = rawResponse; }

  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
