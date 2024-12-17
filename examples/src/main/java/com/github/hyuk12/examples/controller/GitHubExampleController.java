package com.github.hyuk12.examples.controller;

import com.github.hyuk12.examples.dto.GitHubExampleRequest;
import com.hookify.core.pipe.WebhookPipeline;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/github")
@RequiredArgsConstructor
public class GitHubExampleController {
  private final WebhookPipeline webhookPipeline;

  @PostMapping("/webhook")
  public ResponseEntity<String> handleGitHubWebhook(
      @RequestHeader("X-GitHub-Event") String eventType,
      @RequestHeader("X-Hub-Signature-256") String signature,
      @RequestBody GitHubExampleRequest.Request request) {
    try {
      webhookPipeline.execute(eventType, signature, null, request.getPayload());
      return ResponseEntity.ok("Webhook processed successfully!");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Webhook processing failed: " + e.getMessage());
    }
  }
}
