package com.hookify.handlers.slack;

import com.hookify.core.WebhookHandler;
import com.hookify.util.JsonUtils;
import com.hookify.util.slack.SlackApiClient;

public class SlackExtendedHandler implements WebhookHandler {
  private final SlackApiClient apiClient;

  public SlackExtendedHandler(String token) {
    this.apiClient = new SlackApiClient(token);
  }

  @Override
  public void handle(String payload) {
    SlackWebhookPayload event = JsonUtils.fromJson(payload, SlackWebhookPayload.class);
    if ("message".equals(event.getType())) {
      handleEditMessage(event);
    }
  }

  private void handleEditMessage(SlackWebhookPayload event) {
    String channelId = event.getChannel();
    String message = "Updated message content!";
    apiClient.postMessage(channelId, message);
  }
}