package com.example.cryptotrader.web;

import com.example.cryptotrader.redis.ReactivePubSub;
import com.example.cryptotrader.redis.Topics;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/stream")
public class StreamController {

  private final ReactivePubSub pubsub;
  private final Topics topics;

  public StreamController(ReactivePubSub pubsub, Topics topics) {
    this.pubsub = pubsub;
    this.topics = topics;
  }

  @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ServerSentEvent<String>> events() {
    return pubsub.listen(topics.tradeExecuted)
        .map(msg -> ServerSentEvent.builder(msg.getMessage()).event("tradeExecuted").build());
  }
}
