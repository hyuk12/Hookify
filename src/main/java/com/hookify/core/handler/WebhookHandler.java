package com.hookify.core.handler;

public interface WebhookHandler {
  void handle(String eventType, String payload);
}
