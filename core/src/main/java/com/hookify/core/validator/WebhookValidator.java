package com.hookify.core.validator;

public interface WebhookValidator {
  boolean validate(String eventType, String signature, String timestamp, String payload);
}
