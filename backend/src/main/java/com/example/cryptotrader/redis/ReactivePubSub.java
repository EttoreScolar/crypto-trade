package com.example.cryptotrader.redis;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.connection.ReactiveSubscription.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class ReactivePubSub {

  private final ReactiveRedisConnectionFactory factory;

  public ReactivePubSub(ReactiveRedisConnectionFactory factory) {
    this.factory = factory;
  }

  public Flux<Message<String, String>> listen(String channel) {
    var connection = factory.getReactiveConnection();
    return connection.pubSubCommands()
        .subscribe(channel)
        .thenMany(connection.receive());
  }
}
