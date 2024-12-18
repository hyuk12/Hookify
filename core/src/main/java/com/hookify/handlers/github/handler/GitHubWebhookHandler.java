package com.hookify.handlers.github.handler;

import com.hookify.core.handler.WebhookHandler;
import com.hookify.core.logger.WebhookLogFileService;
import com.hookify.core.retry.WebhookRetryManager;
import com.hookify.mapper.github.GitHubDiscordMessageMapper;
import com.hookify.util.JsonUtils;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitHubWebhookHandler implements WebhookHandler {
  private final WebhookLogFileService logFileService = new WebhookLogFileService();
  private static final Logger logger = LoggerFactory.getLogger(GitHubWebhookHandler.class);
  // 활성화된 이벤트 목록 가져오기
  private static final List<String> enabledEvents = GitHubDiscordMessageMapper.getEnabledEvents();


  @Override
  public void handle(String eventType, String payload) {
    // 이벤트 필터링: 활성화된 이벤트만 처리
    if (!enabledEvents.contains(eventType)) {
      logger.info("Event [{}] is not enabled and will be ignored.", eventType);
      return;
    }

    try {
      logger.info("Received event: {}", eventType);
      logger.info("Payload:\n {}", JsonUtils.prettyPrint(payload));

      switch (eventType) {
        case "push" -> handlePushEvent(payload);
        case "pull_request" -> handlePullRequestEvent(payload);
        default -> logFileService.successLog(eventType, payload); // 기본 처리
      }
    } catch (Exception e) {
      logger.error("Failed to handle event:{}", eventType);
      logFileService.failedLog("error", payload);
    }
  }

  private void handlePushEvent(String payload) {
    logger.info("Handling push event...");
    logFileService.successLog("push", payload);
  }

  private void handlePullRequestEvent(String payload) {
    logger.info("Handling pull request event...");
    logFileService.successLog("pull_request", payload);
  }

}