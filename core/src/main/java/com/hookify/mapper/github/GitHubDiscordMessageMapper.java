package com.hookify.mapper.github;

import com.hookify.core.enums.EventType;
import com.hookify.handlers.discord.message.DiscordMessage;
import java.util.List;

public class GitHubDiscordMessageMapper {
  public static DiscordMessage mapToDiscordMessage(String eventType, String payload) {
    DiscordMessage message = new DiscordMessage();
    message.setContent("GitHub Webhook Event: " + eventType);
    message.setUsername("GitHub Webhook");

    // Embed 생성
    DiscordMessage.Embed embed = new DiscordMessage.Embed();
    embed.setTitle("Event: " + eventType);
    embed.setDescription(summarizePayload(payload));
    embed.setColor(determineColor(eventType));

    // Embed 리스트 설정
    message.setEmbeds(List.of(embed));
    return message;
  }

  private static String summarizePayload(String payload) {
    if (payload.length() > 500) { // JSON 길이 제한
      return payload.substring(0, 500) + "...(truncated)";
    }
    return payload;
  }


  private static int determineColor(String eventType) {
    return switch (eventType) {
      case "push" -> 0x00FF00; // Green
      case "pull_request" -> 0x0000FF; // Blue
      default -> 0xFF0000; // Red
    };
  }
}
