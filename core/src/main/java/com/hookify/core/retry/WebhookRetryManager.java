package com.hookify.core.retry;

import com.hookify.core.logger.WebhookLogFileService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebhookRetryManager {
  private static final int MAX_RETRIES = 3; // 최대 재시도 횟수
  private static final long INITIAL_DELAY = 2; // 초기 지연 시간 (초)
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final WebhookLogFileService logService = new WebhookLogFileService();

  public boolean retry(String eventType, String payload, Runnable validationTask) {
    CompletableFuture<Boolean> retryResult = new CompletableFuture<>();
    retryTask(eventType, payload, validationTask, 1, retryResult);

    try {
      return retryResult.get(); // 최종 결과 반환
    } catch (InterruptedException | ExecutionException e) {
      logService.log(eventType, payload, false); // 예외 발생 시 실패 로그 기록
      throw new RuntimeException("Validation retry process interrupted", e);
    }
  }

  private void retryTask(String eventType, String payload, Runnable task, int attempt, CompletableFuture<Boolean> result) {
    if (attempt > MAX_RETRIES) {
      logService.log(eventType, payload, false); // 최대 재시도 횟수 초과 시 실패 로그 기록
      result.complete(false);
      return;
    }

    long delay = INITIAL_DELAY * (1L << (attempt - 1)); // 지수 백오프 적용
    System.out.println("Validation retry attempt " + attempt + " in " + delay + " seconds");

    scheduler.schedule(() -> {
      try {
        task.run();
        result.complete(true);
      } catch (Exception e) {
        System.out.println("Validation attempt " + attempt + " failed: " + e.getMessage());
        retryTask(eventType, payload, task, attempt + 1, result);
      }
    }, delay, TimeUnit.SECONDS);
  }
}