package com.hookify.handlers.github.handler;

import com.hookify.core.WebhookHandler;
import com.hookify.util.JsonUtils;

public class GitHubWebhookHandler implements WebhookHandler {

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
    System.out.println("Push event received for repository: " + event.getRepository().getName());
    System.out.println("Pusher: " + event.getPusher().getName());
  }

  private void handlePullRequestEvent(GitHubWebhookPayload event) {
    System.out.println("Pull request event received: " + event.getPullRequest().getTitle());
    System.out.println("Action: " + event.getAction());
  }
}
