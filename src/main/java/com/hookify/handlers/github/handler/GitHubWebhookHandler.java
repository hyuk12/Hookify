package com.hookify.handlers.github.handler;

import com.hookify.core.handler.WebhookHandler;
import com.hookify.handlers.github.logger.GitHubWebhookLogger;
import com.hookify.util.JsonUtils;

public class GitHubWebhookHandler implements WebhookHandler {

  private final GitHubWebhookLogger logger = new GitHubWebhookLogger();

  @Override
  public void handle(String payload) {
    try {
      GitHubWebhookPayload event = JsonUtils.fromJson(payload,
          GitHubWebhookPayload.class);

      switch (event.getAction()) {
        case "push" -> handlePushEvent(event);
        case "pull_request" -> handlePullRequestEvent(event);
        default -> System.out.println("Unhandled event: " + event.getAction());
      }
    } catch (Exception e) {
      System.out.println("Failed to handle GitHub event: " + e.getMessage());
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
    String repositoryName = event.getRepository().getName();
    String action = event.getAction();
    String prTitle = event.getPullRequest().getTitle();

    // 로그 기록
    logger.logPullRequestEvent(repositoryName, action, prTitle, event.toString());

    // 추가 로직
    System.out.println("Pull request processed for repository: " + repositoryName + ", PR Title: " + prTitle);
  }
}
