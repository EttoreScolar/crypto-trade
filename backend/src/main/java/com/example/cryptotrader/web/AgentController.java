package com.example.cryptotrader.web;

import com.example.cryptotrader.agent.AgentService;
import com.example.cryptotrader.repo.TradeEventRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

  private final AgentService agent;
  private final TradeEventRepository repo;

  public AgentController(AgentService agent, TradeEventRepository repo) {
    this.agent = agent;
    this.repo = repo;
  }

  @PostMapping("/start")
  public void start() { agent.start(); }

  @PostMapping("/stop")
  public void stop() { agent.stop(); }

  @GetMapping("/logs")
  public Object logs() {
    return repo.findTop200ByOrderByIdDesc();
  }
}
