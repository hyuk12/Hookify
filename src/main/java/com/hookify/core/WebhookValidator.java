package com.hookify.core;

public interface WebhookValidator {
  boolean validate(String signature, String timestamp, String payload);
}
