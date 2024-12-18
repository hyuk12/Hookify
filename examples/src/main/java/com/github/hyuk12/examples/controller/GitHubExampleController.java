package com.github.hyuk12.examples.controller;

import com.github.hyuk12.boot.global.config.WebhookConfig;
import com.hookify.core.pipe.WebhookPipeline;
import com.hookify.handlers.discord.processor.DiscordProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/github")
public class GitHubExampleController {
  private final WebhookPipeline webhookPipeline;
  private final DiscordProcessor discordProcessor;

  @Autowired
  public GitHubExampleController(WebhookPipeline webhookPipeline, DiscordProcessor discordProcessor) {
    this.webhookPipeline = webhookPipeline;
    this.discordProcessor = discordProcessor;
  }

  @PostMapping("/webhook")
  public ResponseEntity<String> handleGitHubWebhook(
      @RequestHeader("X-GitHub-Event") String eventType,
      @RequestHeader("X-Hub-Signature-256") String signature,
      @RequestBody String payload) {
    try {
      webhookPipeline.addPostProcessor(discordProcessor);
      webhookPipeline.execute(eventType, signature, null, payload);
      return ResponseEntity.ok("Webhook processed successfully!");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Webhook processing failed: " + e.getMessage());
    }
  }
}
