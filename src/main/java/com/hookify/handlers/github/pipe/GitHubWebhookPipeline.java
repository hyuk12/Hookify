package com.hookify.handlers.github.pipe;

import com.hookify.core.PostProcessor;
import com.hookify.core.handler.WebhookHandler;
import com.hookify.core.retry.WebhookRetryManager;
import com.hookify.core.validator.WebhookValidator;
import com.hookify.core.pipe.WebhookPipeline;
import com.hookify.handlers.github.handler.GitHubWebhookHandler;
import com.hookify.handlers.github.processor.GitHubProcessor;
import com.hookify.handlers.github.validator.GitHubWebhookValidator;

public class GitHubWebhookPipeline {
  private static final WebhookRetryManager retryManager = new WebhookRetryManager();

  public static WebhookPipeline create(String secret) {
    WebhookValidator validator = new GitHubWebhookValidator(secret);
    WebhookHandler handler = new GitHubWebhookHandler();
    PostProcessor postProcessor = new GitHubProcessor();

    return new WebhookPipeline()
        .validator((signature, timestamp, payload) -> {
          boolean success = retryManager.retry(() -> {
            if (!validator.validate(signature, timestamp, payload)) {
              throw new IllegalStateException("Secret validation failed");
            }
          });
          if (!success) {
            throw new IllegalStateException("Secret validation failed after retries");
          }
          return true;
        })  // Secret 검증 및 재시도
        .handleEvent(handler)       // 이벤트 처리
        .postProcess(postProcessor); // 후속 작업
  }
}
