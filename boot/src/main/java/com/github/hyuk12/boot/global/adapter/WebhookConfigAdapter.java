package com.github.hyuk12.boot.global.adapter;

import com.github.hyuk12.boot.global.config.WebhookConfig;
import com.hookify.mapper.github.GitHubDiscordMessageMapper;

public class WebhookConfigAdapter {
  private final WebhookConfig webhookConfig;

  public WebhookConfigAdapter(WebhookConfig webhookConfig) {
    this.webhookConfig = webhookConfig;
  }

  public void applyToMapper() {
    GitHubDiscordMessageMapper.setEventFilters(webhookConfig.getFilters());
  }


}
