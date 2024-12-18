package com.hookify.handlers.discord.processor;

import com.hookify.core.PostProcessor;
import com.hookify.handlers.discord.message.DiscordMessage;
import com.hookify.handlers.discord.message.DiscordNotifier;
import com.hookify.mapper.github.GitHubDiscordMessageMapper;

public class DiscordProcessor implements PostProcessor {
  private final String webhookUrl;

  public DiscordProcessor(String webhookUrl) {
    this.webhookUrl = webhookUrl;
  }

  public static DiscordProcessor create(String webhookUrl) {
    return new DiscordProcessor(webhookUrl);
  }

  @Override
  public void process(String eventType, String payload) {
    DiscordMessage discordMessage = GitHubDiscordMessageMapper.mapToDiscordMessage(eventType,
        payload);
    // send message to Discord
    DiscordNotifier.sendMessage(webhookUrl, discordMessage);
  }
}
