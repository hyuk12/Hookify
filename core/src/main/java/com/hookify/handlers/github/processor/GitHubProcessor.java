package com.hookify.handlers.github.processor;

import com.hookify.core.PostProcessor;
import com.hookify.handlers.github.handler.GitHubWebhookHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitHubProcessor implements PostProcessor {
  private static final Logger logger = LoggerFactory.getLogger(GitHubProcessor.class);

  @Override
  public void process(String eventType, String payload) {
    logger.info("Processing GitHub payload: {}", payload);
  }
}
