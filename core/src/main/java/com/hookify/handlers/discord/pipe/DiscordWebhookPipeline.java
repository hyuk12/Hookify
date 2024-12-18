package com.hookify.handlers.discord.pipe;

import com.hookify.core.PostProcessor;
import com.hookify.core.handler.WebhookHandler;
import com.hookify.core.pipe.WebhookPipeline;
import com.hookify.core.retry.WebhookRetryManager;
import com.hookify.core.validator.WebhookValidator;
import com.hookify.handlers.discord.handler.DiscordWebhookHandler;
import com.hookify.handlers.discord.validator.DiscordWebhookValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordWebhookPipeline {
  private static final WebhookRetryManager retryManager = new WebhookRetryManager();
  private static final Logger logger = LoggerFactory.getLogger(DiscordWebhookPipeline.class);

  public static WebhookPipeline create(String secret) {
    WebhookValidator validator = new DiscordWebhookValidator(secret);
    WebhookHandler handler = new DiscordWebhookHandler();
    PostProcessor postProcessor = payload -> System.out.println("Post-processing Discord payload...");

    return new WebhookPipeline()
        .validator((eventType, signature, timestamp, payload) -> {
          retryManager.retry(eventType, payload, () -> {
            if (!validator.validate(eventType, signature, timestamp, payload)) {
              throw new IllegalStateException("Validation failed for Discord webhook.");
            }
          });
          return true;
        })
        .handleEvent(handler)
        .postProcess(postProcessor);
  }

}
