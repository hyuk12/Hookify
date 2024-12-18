package com.hookify.handlers.discord.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hookify.core.handler.WebhookHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordWebhookHandler implements WebhookHandler {
  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger logger = LoggerFactory.getLogger(DiscordWebhookHandler.class);
  @Override
  public void handle(String eventType, String payload) {
    try {
      JsonNode rootNode = objectMapper.readTree(payload);

      if (rootNode.has("content")) {
        logger.info("Received Discord Message: {}", rootNode.get("content").asText());
      }

      // Embed 메시지 처리
      if (rootNode.has("embeds")) {
        for (JsonNode embed : rootNode.get("embeds")) {
          logger.info("Embed Title: {}", embed.get("title").asText());
          logger.info("Embed Description: {}", embed.get("description").asText());
        }
      }
    } catch (JsonProcessingException e) {
      logger.error("Failed to handle event:{}", eventType);
      throw new RuntimeException(e);
    }
  }
}
