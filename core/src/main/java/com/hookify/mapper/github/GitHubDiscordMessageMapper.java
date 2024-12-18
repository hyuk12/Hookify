package com.hookify.mapper.github;

import com.hookify.core.enums.EventType;
import com.hookify.handlers.discord.message.DiscordMessage;
import java.util.List;

public class GitHubDiscordMessageMapper {
  public static DiscordMessage mapToDiscordMessage(String eventType, String payload) {
    if (eventType.contains(EventType.SUCCESS.name())) {
      return null;
    }
    DiscordMessage message = new DiscordMessage();
    message.setContent("GitHub Webhook Event: " + eventType);
    message.setUsername("GitHub Webhook");

    DiscordMessage.Embed embed = new DiscordMessage.Embed();
    embed.setTitle("GitHub Event: " + eventType);
    embed.setDescription("Payload: " + payload);
    embed.setColor(7506394);

    message.setEmbeds(List.of(embed));
    return message;
  }
}
