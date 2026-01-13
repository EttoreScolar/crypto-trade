package com.example.cryptotrader.repo;

import com.example.cryptotrader.domain.TradeEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TradeEventRepository extends JpaRepository<TradeEvent, Long> {
  List<TradeEvent> findTop200ByOrderByIdDesc();
}
