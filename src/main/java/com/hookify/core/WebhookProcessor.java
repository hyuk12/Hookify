package com.hookify.core;

import java.util.HashMap;
import java.util.Map;

public class WebhookProcessor {
  private final Map<String, WebhookHandler> handlers = new HashMap<>();
  private final Map<String, WebhookValidator> validators = new HashMap<>();

  public void registerHandler(String event, WebhookHandler handler) {
    handlers.put(event, handler);
  }

  public void process(String event, String signature, String timestamp, String payload) {
    WebhookValidator validator = validators.get(event);
    if (validator != null && !validator.validate(signature, timestamp, payload)) {
      System.out.println("Validation failed for event: " + event);
      return;
    }

    WebhookHandler handler = handlers.get(event);
    if (handler != null) {
      handler.handle(payload);
    } else {
      System.out.println("No handler found for event: " + event);
    }
  }
}
