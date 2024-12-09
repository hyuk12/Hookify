package com.hookify.util.slack;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SlackApiClient {
  private final String token;

  // 생성자를 통해 토큰 주입
  public SlackApiClient(String token) {
    if (token == null || token.isEmpty()) {
      throw new IllegalArgumentException("Slack API 토큰은 null이거나 비어 있을 수 없습니다.");
    }
    this.token = token;
  }

  public void postMessage(String channel, String text) {
    try {
      URL url = new URL("https://slack.com/api/chat.postMessage");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Authorization", "Bearer " + token);
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setDoOutput(true);

      String payload = String.format("{\"channel\":\"%s\",\"text\":\"%s\"}", channel, text);

      try (OutputStream os = connection.getOutputStream()) {
        os.write(payload.getBytes());
        os.flush();
      }

      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        System.out.println("Message sent successfully to Slack!");
      } else {
        System.out.println("Failed to send message to Slack: HTTP " + responseCode);
      }
    } catch (Exception e) {
      System.out.println("Error sending message to Slack: " + e.getMessage());
    }
  }

}
