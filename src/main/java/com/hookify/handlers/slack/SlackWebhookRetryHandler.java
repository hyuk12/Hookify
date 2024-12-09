package com.hookify.handlers.slack;

import com.hookify.core.RetryPolicy;

public class SlackWebhookRetryHandler {
  private final RetryPolicy retryPolicy;

  public SlackWebhookRetryHandler(RetryPolicy retryPolicy) {
    this.retryPolicy = retryPolicy;
  }

  public void retry(Runnable task) {
    int attempts = 0;
    while (attempts < retryPolicy.getMaxRetries()) {
      try {
        task.run();
        return;
      } catch (Exception e) {
        attempts++;
        System.out.println("Retry attempt " + attempts + " failed");
        try {
          Thread.sleep(retryPolicy.getRetryDelay());
        } catch (InterruptedException ignored) {
        }
      }
    }
    System.out.println("All retry attempts failed");
  }
}
