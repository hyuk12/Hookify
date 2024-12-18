package com.github.hyuk12.boot.global.config.github;

import com.github.hyuk12.boot.global.adapter.WebhookConfigAdapter;
import com.github.hyuk12.boot.global.config.WebhookConfig;
import com.github.hyuk12.boot.global.properties.DiscordWebhookProperties;
import com.hookify.handlers.discord.processor.DiscordProcessor;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties({DiscordWebhookProperties.class, WebhookConfig.class})
public class DiscordWebhookAuthConfiguration {

  @Bean(name = "webhookConfig")
  public WebhookConfig webhookConfig() {
    return new WebhookConfig();
  }

  @Bean
  @ConditionalOnMissingBean
  public DiscordProcessor discordProcessor(
      DiscordWebhookProperties properties,
      @Qualifier("webhookConfig") WebhookConfig webhookConfig) {

    // WebhookConfigAdapter를 통해 필터 적용
    WebhookConfigAdapter adapter = new WebhookConfigAdapter(webhookConfig);
    adapter.applyToMapper();

    if (Objects.isNull(properties.getUrl()) || properties.getUrl().isEmpty()) {
      throw new IllegalArgumentException("Discord Webhook URL cannot be null or empty");
    }
    return DiscordProcessor.create(properties.getUrl());
  }
}
