package com.example.cryptotrader.web;

import com.example.cryptotrader.repo.TradeEventRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class TradeEventController {

  private final TradeEventRepository repo;

  public TradeEventController(TradeEventRepository repo) {
    this.repo = repo;
  }

  @GetMapping("/latest")
  public Object latest() {
    return repo.findTop200ByOrderByIdDesc();
  }
}
