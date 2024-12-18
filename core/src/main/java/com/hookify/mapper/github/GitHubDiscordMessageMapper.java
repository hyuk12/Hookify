package com.hookify.mapper.github;

import com.hookify.core.enums.EventType;
import com.hookify.handlers.discord.message.DiscordMessage;
import java.util.List;

public class GitHubDiscordMessageMapper {
  public static DiscordMessage mapToDiscordMessage(String eventType, String payload) {
    DiscordMessage message = new DiscordMessage();
    message.setContent("GitHub Webhook Event: " + eventType);
    message.setUsername("GitHub Webhook");

    DiscordMessage.Embed embed = new DiscordMessage.Embed();
    embed.setTitle("Event: " + eventType);
    embed.setDescription(payload); // JSON 페이로드 그대로 전달
    embed.setColor(7506394);

    message.setEmbeds(List.of(embed));
    return message;
  }

  private static int determineColor(String eventType) {
    return switch (eventType) {
      case "push" -> 0x00FF00; // Green
      case "pull_request" -> 0x0000FF; // Blue
      default -> 0xFF0000; // Red
    };
  }
}
