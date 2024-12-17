package com.hookify.handlers.github.handler;

import com.hookify.core.handler.WebhookHandler;
import com.hookify.core.logger.WebhookLogFileService;
import com.hookify.handlers.github.logger.GitHubWebhookLogger;
import com.hookify.util.JsonUtils;

public class GitHubWebhookHandler implements WebhookHandler {
  private final WebhookLogFileService logFileService = new WebhookLogFileService();

  @Override
  public void handle(String eventType, String payload) {
    try {
      System.out.println("Received event: " + eventType);
      System.out.println("Payload:\n" + JsonUtils.prettyPrint(payload));

      switch (eventType) {
        case "push" -> handlePushEvent(payload);
        case "pull_request" -> handlePullRequestEvent(payload);
        default -> logFileService.log(eventType, payload, true); // 기본 처리
      }
    } catch (Exception e) {
      System.out.println("Failed to handle event: " + eventType);
      logFileService.log("error", payload, false);
    }
  }

  private void handlePushEvent(String payload) {
    System.out.println("Handling push event...");
    logFileService.log("push", payload, true);
  }

  private void handlePullRequestEvent(String payload) {
    System.out.println("Handling pull request event...");
    logFileService.log("pull_request", payload, true);
  }

}