package com.github.hyuk12.boot.global.config.github;

import com.github.hyuk12.boot.global.properties.DiscordWebhookProperties;
import com.github.hyuk12.boot.global.properties.WebhookProperties;
import com.hookify.core.pipe.WebhookPipeline;
import com.hookify.handlers.discord.processor.DiscordProcessor;
import com.hookify.handlers.github.pipe.GitHubWebhookPipeline;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({WebhookProperties.class, DiscordWebhookProperties.class})
public class GitHubWebhookAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public WebhookPipeline webhookPipeline(WebhookProperties properties, DiscordWebhookProperties discordWebhookProperties) {
    return GitHubWebhookPipeline.create(properties.getSecret(), DiscordProcessor.create(discordWebhookProperties.getUrl()));
  }
}
