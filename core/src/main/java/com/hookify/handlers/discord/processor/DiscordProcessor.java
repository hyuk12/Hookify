package com.hookify.handlers.discord.processor;

import com.hookify.core.PostProcessor;
import com.hookify.handlers.discord.message.DiscordMessage;
import com.hookify.handlers.discord.message.DiscordNotifier;
import com.hookify.mapper.github.GitHubDiscordMessageMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordProcessor implements PostProcessor {
  private final String webhookUrl;
  private static final List<String> enabledEvents = GitHubDiscordMessageMapper.getEnabledEvents();
  private static final Logger logger = LoggerFactory.getLogger(DiscordProcessor.class);

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
    // 이벤트 필터링: 활성화된 이벤트만 처리
    if (!enabledEvents.contains(eventType)) {
      logger.info("Event [{}] is not enabled and will be ignored.", eventType);
      return;
    }

    DiscordNotifier.sendMessage(webhookUrl, discordMessage);
  }
}
