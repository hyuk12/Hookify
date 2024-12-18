package com.github.hyuk12.examples.discord;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hookify.core.pipe.WebhookPipeline;
import com.hookify.handlers.discord.processor.DiscordProcessor;
import com.hookify.handlers.github.pipe.GitHubWebhookPipeline;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class DiscordWebhookExample {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static void main(String[] args) throws IOException {
    String githubSecret = "";
    String discordWebhookUrl = "";

    WebhookPipeline pipeline = GitHubWebhookPipeline.create(githubSecret);
    DiscordProcessor discordProcessor = DiscordProcessor.create(discordWebhookUrl);

    pipeline.addPostProcessor(discordProcessor);

    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
    server.createContext("/github/webhook", exchange -> {
      if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
        byte[] requestBody = exchange.getRequestBody().readAllBytes();
        String payload = new String(requestBody);
        String signature = exchange.getRequestHeaders().getFirst("X-Hub-Signature-256");
        String eventType = exchange.getRequestHeaders().getFirst("X-GitHub-Event"); // 이벤트 타입

        try {
          // WebhookPipeline 실행
          pipeline.execute(eventType, signature, null, payload);

          // JSON 데이터 파싱
          JsonNode rootNode = objectMapper.readTree(payload);
          String sender = rootNode.path("sender").path("login").asText(); // 이벤트 발생 주체
          String repository = rootNode.path("repository").path("full_name").asText();
          String message = String.format("✅ [%s] 이벤트 성공: %s by %s", eventType, repository, sender);


          exchange.sendResponseHeaders(200, message.length());
        } catch (Exception e) {
          String errorMessage = String.format("❌ [%s] 이벤트 실패: %s", eventType, e.getMessage());
          exchange.sendResponseHeaders(400, errorMessage.length());
        }
        exchange.getResponseBody().close();
      } else {
        exchange.sendResponseHeaders(405, -1);
      }
    });
    server.start();
    System.out.println("Discord Webhook Example Server is running...");
  }
}
