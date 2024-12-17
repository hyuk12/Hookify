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
        .validator((eventType, signature, timestamp, payload) -> {
          retryManager.retry(eventType, payload, () -> {
            if (!validator.validate(eventType, signature, timestamp, payload)) {
              throw new IllegalStateException("Signature validation failed");
            }
          });
          return true;
        })
        .handleEvent(handler)
        .postProcess(postProcessor);
  }
}
