package com.hookify.core.logger;

public interface WebhookLogger {
  void logSuccess(String event, String payload);
  void logFailure(String event, String reason, String payload);
}
