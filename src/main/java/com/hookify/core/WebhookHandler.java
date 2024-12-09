package com.hookify.core;

public interface WebhookHandler {
  void handle(String payload);
}
