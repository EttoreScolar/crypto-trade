package com.example.cryptotrader.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "trade_event")
public class TradeEvent {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String env;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_type", nullable = false)
  private TradeEventType eventType;

  @Column(nullable = false)
  private String symbol;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TradeSide side;

  @Column
  private Double amount;

  @Column
  private Double confidence;

  @Column
  private String rationale;

  @Column
  private String reason;

  @Column(columnDefinition = "jsonb")
  private String payload;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getEnv() { return env; }
  public void setEnv(String env) { this.env = env; }

  public TradeEventType getEventType() { return eventType; }
  public void setEventType(TradeEventType eventType) { this.eventType = eventType; }

  public String getSymbol() { return symbol; }
  public void setSymbol(String symbol) { this.symbol = symbol; }

  public TradeSide getSide() { return side; }
  public void setSide(TradeSide side) { this.side = side; }

  public Double getAmount() { return amount; }
  public void setAmount(Double amount) { this.amount = amount; }

  public Double getConfidence() { return confidence; }
  public void setConfidence(Double confidence) { this.confidence = confidence; }

  public String getRationale() { return rationale; }
  public void setRationale(String rationale) { this.rationale = rationale; }

  public String getReason() { return reason; }
  public void setReason(String reason) { this.reason = reason; }

  public String getPayload() { return payload; }
  public void setPayload(String payload) { this.payload = payload; }

  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
