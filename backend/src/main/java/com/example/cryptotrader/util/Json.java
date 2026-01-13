package com.example.cryptotrader.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class Json {
  private final ObjectMapper mapper = new ObjectMapper();

  public String toJson(Object o) {
    try { return mapper.writeValueAsString(o); }
    catch (Exception e) { throw new RuntimeException(e); }
  }

  public <T> T fromJson(String s, Class<T> type) {
    try { return mapper.readValue(s, type); }
    catch (Exception e) { throw new RuntimeException(e); }
  }
}
