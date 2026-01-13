package com.example.cryptotrader.agent;

import com.example.cryptotrader.domain.TradeEvent;
import com.example.cryptotrader.repo.TradeEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class AgentService {

  private volatile boolean running = false;

  private final Strategy strategy;
  private final RiskManager risk;
  private final ExecutionService exec;
  private final TradeEventRepository tradeRepo;

  public AgentService(Strategy strategy, RiskManager risk, ExecutionService exec, TradeEventRepository tradeRepo) {
    this.strategy = strategy;
    this.risk = risk;
    this.exec = exec;
    this.tradeRepo = tradeRepo;
  }

  public void start() { running = true; }
  public void stop() { running = false; }

  @Scheduled(fixedDelay = 1000)
  public void tick() {
    if (!running) return;

    AgentState state = exec.currentState();
    Decision decision = strategy.evaluate(state);
    RiskVerdict verdict = risk.validate(decision, state);

    TradeEvent event = new TradeEvent();
    event.setUserId(state.userId());
    event.setEnv(state.env());
    event.setSymbol(decision.symbol());
    event.setDecision(decision.type());
    event.setRiskAllowed(verdict.allowed());
    event.setReason(verdict.reason());

    if (verdict.allowed()) {
      String raw = exec.execute(decision);
      event.setRawResponse(raw);
    }

    tradeRepo.save(event);
  }
}
