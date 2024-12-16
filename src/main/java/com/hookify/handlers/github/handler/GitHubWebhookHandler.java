package com.hookify.handlers.github.handler;

import com.hookify.core.handler.WebhookHandler;
import com.hookify.handlers.github.logger.GitHubWebhookLogger;
import com.hookify.util.JsonUtils;

public class GitHubWebhookHandler implements WebhookHandler {

  private final GitHubWebhookLogger logger = new GitHubWebhookLogger();

  @Override
  public void handle(String payload) {
    try {
      GitHubWebhookPayload event = JsonUtils.fromJson(payload, GitHubWebhookPayload.class);

      // 이벤트 타입 판단
      String eventType = determineEventType(payload);

      switch (eventType) {
        case "push" -> handlePushEvent(event);
        case "pull_request" -> handlePullRequestEvent(event);
        default -> System.out.println("Unhandled event type: " + eventType);
      }
    } catch (Exception e) {
      System.out.println("Failed to handle GitHub event: " + e.getMessage());
    }
  }

  private String determineEventType(String payload) {
    try {
      // payload 내 특정 필드를 기반으로 이벤트 타입 판단
      if (payload.contains("\"pull_request\":")) {
        return "pull_request";
      } else if (payload.contains("\"ref\":") && payload.contains("\"before\":")) {
        return "push";
      }
      return "unknown";
    } catch (Exception e) {
      throw new RuntimeException("Failed to determine event type: " + e.getMessage());
    }
  }

  private void handlePushEvent(GitHubWebhookPayload event) {
    String repositoryName = event.getRepository().getName();
    String pusherName = event.getPusher().getName();

    // 로그 기록
    logger.logPushEvent(repositoryName, pusherName, event.toString());

    // 추가 로직
    System.out.println("Push event processed for repository: " + repositoryName);
  }

  private void handlePullRequestEvent(GitHubWebhookPayload event) {
    if (event.getPullRequest() == null) {
      System.out.println("Pull request data is missing!");
      return;
    }

    String repositoryName = event.getRepository().getName();
    String action = event.getAction();
    String prTitle = event.getPullRequest().getTitle();

    // 로그 기록
    logger.logPullRequestEvent(repositoryName, action, prTitle, event.toString());

    // 추가 로직
    System.out.println("Pull request processed for repository: " + repositoryName + ", PR Title: " + prTitle);
  }
}
