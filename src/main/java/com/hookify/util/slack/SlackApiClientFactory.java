package com.hookify.util.slack;

public class SlackApiClientFactory {
  public static SlackApiClient fromEnvironment() {
    String token = System.getenv("SLACK_BOT_TOKEN");
    if (token == null || token.isEmpty()) {
      throw new IllegalStateException("SLACK_BOT_TOKEN 환경 변수가 설정되지 않았습니다.");
    }
    return new SlackApiClient(token);
  }
}
