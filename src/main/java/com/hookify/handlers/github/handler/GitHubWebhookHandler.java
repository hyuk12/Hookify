package com.hookify.handlers.github.handler;

import com.hookify.core.handler.WebhookHandler;
import com.hookify.handlers.github.logger.GitHubWebhookLogger;
import com.hookify.util.JsonUtils;

public class GitHubWebhookHandler implements WebhookHandler {

  private final GitHubWebhookLogger logger = new GitHubWebhookLogger();

  @Override
  public void handle(String payload) {
    try {
      System.out.println("Webhook Payload:\n" + JsonUtils.prettyPrint(payload));

      GitHubWebhookPayload event = JsonUtils.fromJson(payload, GitHubWebhookPayload.class);
      if (event == null || event.getRepository() == null) {
        throw new IllegalArgumentException("Invalid or missing GitHub webhook payload.");
      }

      if (event.getPullRequest() != null) {
        handlePullRequestEvent(event);
      } else if (event.getCommits() != null) {
        handlePushEvent(event);
      } else {
        System.out.println("Unhandled event type.");
      }
    } catch (Exception e) {
      System.out.println("Failed to handle GitHub event: " + e.getMessage());
    }
  }

  private void handlePushEvent(GitHubWebhookPayload event) {
    String repositoryName = event.getRepository().getName();
    String pusherName = event.getPusher().getName();

    // 로그 기록
    logger.logPushEvent(repositoryName, pusherName, event.getCommits());

    // 콘솔 출력
    System.out.println("Processed push event for repository: " + repositoryName);
  }

  private void handlePullRequestEvent(GitHubWebhookPayload event) {
    String repositoryName = event.getRepository().getName();
    String action = event.getAction();
    String prTitle = event.getPullRequest().getTitle();

    // 로그 기록
    logger.logPullRequestEvent(repositoryName, action, prTitle);

    // 콘솔 출력
    System.out.println("Processed pull request for repository: " + repositoryName + ", Action: " + action);
  }
}
