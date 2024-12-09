package com.hookify.handlers.github;

import com.hookify.core.WebhookHandler;

public class GitHubWebhookHandler implements WebhookHandler {

  @Override
  public void handle(String payload) {
    System.out.println("GitHub 이벤트 처리: " + payload);
  }
}
