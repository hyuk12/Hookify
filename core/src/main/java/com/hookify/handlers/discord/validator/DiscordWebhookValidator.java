package com.hookify.handlers.discord.validator;

import com.hookify.core.validator.WebhookValidator;
import com.hookify.handlers.discord.handler.DiscordWebhookHandler;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DiscordWebhookValidator implements WebhookValidator {
  private final String secret;
  private static final Logger logger = LoggerFactory.getLogger(DiscordWebhookValidator.class);

  public DiscordWebhookValidator(String secret) {
    this.secret = secret;
  }

  @Override
  public boolean validate(String eventType, String signature, String timestamp, String payload) {
    // Discord는 기본적으로 시그니처 검증을 요구하지 않지만, 확장을 위해 추가
    if (Objects.isNull(payload) || payload.isEmpty()) {
      throw new IllegalArgumentException("Payload cannot be null or empty");
    }
    logger.info("Payload validated successfully.");
    return true;
  }
}
