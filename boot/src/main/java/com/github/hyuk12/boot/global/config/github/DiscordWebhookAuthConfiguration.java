package com.github.hyuk12.boot.global.config.github;

import com.github.hyuk12.boot.global.properties.DiscordWebhookProperties;
import com.hookify.handlers.discord.processor.DiscordProcessor;
import java.util.Objects;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DiscordWebhookProperties.class)
public class DiscordWebhookAuthConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public DiscordProcessor discordProcessor(DiscordWebhookProperties properties) {
    if (Objects.isNull(properties.getUrl()) || properties.getUrl().isEmpty()) {
      throw new IllegalArgumentException("Discord Webhook URL cannot be null or empty");
    }
    return DiscordProcessor.create(properties.getUrl());
  }
}
