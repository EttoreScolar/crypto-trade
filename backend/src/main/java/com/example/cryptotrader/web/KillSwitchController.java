package com.example.cryptotrader.web;

import com.example.cryptotrader.risk.KillSwitchService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/killswitch")
public class KillSwitchController {

  private final KillSwitchService kill;

  public KillSwitchController(KillSwitchService kill) {
    this.kill = kill;
  }

  @GetMapping
  public Mono<Boolean> get() {
    return kill.isEnabled();
  }

  @PostMapping("/enable")
  public Mono<Boolean> enable() {
    return kill.setEnabled(true);
  }

  @PostMapping("/disable")
  public Mono<Boolean> disable() {
    return kill.setEnabled(false);
  }
}
