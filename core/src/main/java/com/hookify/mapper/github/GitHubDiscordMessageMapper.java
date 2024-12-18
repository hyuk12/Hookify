package com.hookify.mapper.github;

import com.hookify.handlers.discord.message.DiscordMessage;
import java.util.List;

public class GitHubDiscordMessageMapper {
  public static DiscordMessage mapToDiscordMessage(String eventType, String payload) {
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
