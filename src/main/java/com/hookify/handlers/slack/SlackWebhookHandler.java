package com.hookify.handlers.slack;

import com.hookify.core.WebhookHandler;
import com.hookify.util.JsonUtils;
import com.hookify.util.slack.SlackApiClient;

public class SlackWebhookHandler implements WebhookHandler {
  private final SlackApiClient apiClient;

  // 생성자에서 SlackApiClient를 초기화
  public SlackWebhookHandler(String slackBotToken) {
    this.apiClient = new SlackApiClient(slackBotToken);
  }

  @Override
  public void handle(String payload) {
    try {
      SlackWebhookPayload event = JsonUtils.fromJson(payload, SlackWebhookPayload.class);

      switch (event.getType()) {
        case "message" -> handleMessageEvent(event);
        default -> System.out.println("Unhandled event type: " + event.getType());
      }
    } catch (Exception e) {
      System.out.println("Error processing Slack event: " + e.getMessage());
    }
  }

  private void handleMessageEvent(SlackWebhookPayload event) {
    System.out.println("새 메시지: " + event.getText());
    System.out.println("사용자: " + event.getUser());
    System.out.println("채널: " + event.getChannel());

    // Slack API 클라이언트를 사용해 응답 메시지 전송
    apiClient.postMessage(event.getChannel(), "메시지를 받았습니다: " + event.getText());
  }
}
