package com.hookify.handlers.github.pipe;

import com.hookify.core.PostProcessor;
import com.hookify.core.handler.WebhookHandler;
import com.hookify.core.validator.WebhookValidator;
import com.hookify.core.pipe.WebhookPipeline;
import com.hookify.handlers.github.handler.GitHubWebhookHandler;
import com.hookify.handlers.github.processor.GitHubProcessor;
import com.hookify.handlers.github.validator.GitHubWebhookValidator;

public class GitHubWebhookPipeline {
  public static WebhookPipeline create(String secret) {
    WebhookValidator validator = new GitHubWebhookValidator(secret);
    WebhookHandler handler = new GitHubWebhookHandler();
    PostProcessor postProcessor = new GitHubProcessor();

    return new WebhookPipeline()
        .validator(validator)   // Secret 검증
        .handleEvent(handler)      // 이벤트 처리
        .postProcess(postProcessor); // 후속 작업
  }
}
