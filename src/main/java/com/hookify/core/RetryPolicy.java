package com.hookify.core;

public class RetryPolicy {
  private final int maxRetries;
  private final long retryDelay;

  public RetryPolicy(int maxRetries, long retryDelay) {
    this.maxRetries = maxRetries;
    this.retryDelay = retryDelay;
  }

  public int getMaxRetries() {
    return maxRetries;
  }

  public long getRetryDelay() {
    return retryDelay;
  }
}
