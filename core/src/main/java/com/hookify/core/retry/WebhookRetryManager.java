package com.hookify.core.retry;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebhookRetryManager {
  private static final int MAX_RETRIES = 3; // 최대 재시도 횟수
  private static final long INITIAL_DELAY = 2; // 초기 지연 시간 (초)

  public void retry(Runnable validationTask) {
    for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
      try {
        validationTask.run();
        return; // 성공 시 메서드 종료
      } catch (Exception e) {
        System.out.println("Validation attempt " + attempt + " failed: " + e.getMessage());
        if (attempt == MAX_RETRIES) {
          throw new IllegalStateException("Validation failed after " + MAX_RETRIES + " retries", e);
        }
        try {
          long delay = INITIAL_DELAY * (1L << (attempt - 1)); // 지수 백오프 적용
          System.out.println("Retrying in " + delay + " seconds...");
          Thread.sleep(delay * 1000); // 딜레이 적용
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          throw new RuntimeException("Retry process interrupted", ie);
        }
      }
    }
  }
}