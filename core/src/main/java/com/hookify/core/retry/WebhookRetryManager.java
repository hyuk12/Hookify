package com.hookify.core.retry;

import com.hookify.core.logger.WebhookLogFileService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebhookRetryManager {
  private static final int MAX_RETRIES = 3; // 최대 재시도 횟수
  private static final long INITIAL_DELAY = 2; // 초기 지연 시간 (초)
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final WebhookLogFileService logService = new WebhookLogFileService();
  private static final Logger logger = LoggerFactory.getLogger(WebhookRetryManager.class);

  public void retry(String eventType, String payload, Runnable validationTask) {
    for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
      try {
        validationTask.run();
        return; // 성공하면 리턴
      } catch (Exception e) {
        logService.logRetry(eventType, payload, attempt, e); // 실패 시 재시도 로그
        if (attempt == MAX_RETRIES) {
          throw new IllegalStateException("Validation failed after retries", e);
        }
        try {
          Thread.sleep((long) Math.pow(2, attempt) * 1000); // 지수 백오프
        } catch (InterruptedException interruptedException) {
          Thread.currentThread().interrupt();
          logService.logRetry(eventType, payload, attempt, e); // 실패한 경우 로그 기록
          throw new IllegalStateException("Retry interrupted", interruptedException);
        }
      }
    }
  }

  private void retryTask(String eventType, String payload, Runnable task, int attempt, CompletableFuture<Void> result) {
    if (attempt > MAX_RETRIES) {
      result.completeExceptionally(new IllegalStateException("Validation failed after retries"));
      return;
    }

    long delay = INITIAL_DELAY * (1L << (attempt - 1)); // 지수 백오프 적용
    logger.info("Validation retry attempt: {} in delay: {} seconds", attempt, delay);

    scheduler.schedule(() -> {
      try {
        task.run();
        result.complete(null); // 성공 시 완료
      } catch (Exception e) {
        logger.error("Validation attempt {} failed: {}", attempt, e.getMessage());
        logService.logRetry(eventType, payload, attempt, e); // 실패한 경우 로그 기록
        retryTask(eventType, payload, task, attempt + 1, result);
      }
    }, delay, TimeUnit.SECONDS);
  }
}