package com.hookify.examples.github;

import com.hookify.core.pipe.WebhookPipeline;
import com.hookify.handlers.github.pipe.GitHubWebhookPipeline;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class GitHubWebhookExample {

  public static void main(String[] args) throws IOException {
    // Secret 설정
    String secret = "ACM+ZoVPpAeRzEVTzYfKlxYChb7DroCT11uTXw23to8=";

    // GitHub WebhookPipeline 생성
    WebhookPipeline pipeline = GitHubWebhookPipeline.create(secret);

    // HTTP 서버 초기화
    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
    server.createContext("/github/webhook", exchange -> handleRequest(exchange, pipeline));
    server.start();

    System.out.println("GitHub Webhook 서버가 8080 포트에서 실행 중....");
  }

  private static void handleRequest(HttpExchange exchange, WebhookPipeline pipeline) throws IOException {
    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      byte[] requestBody = exchange.getRequestBody().readAllBytes();
      String payload = new String(requestBody);
      String signature = exchange.getRequestHeaders().getFirst("X-Hub-Signature-256");
      String eventType = exchange.getRequestHeaders().getFirst("X-GitHub-Event"); // 이벤트 타입 읽기

      System.out.println("Webhook 요청 수신:");
      System.out.println("Event: " + eventType);
      System.out.println("Signature: " + signature);
      System.out.println("Payload: " + payload);

      try {
        pipeline.execute(eventType, signature, null, payload); // 이벤트 타입 전달

        String response = "Webhook processed successfully";
        exchange.sendResponseHeaders(200, response.length());
        exchange.getResponseBody().write(response.getBytes());
      } catch (Exception e) {
        String response = "Webhook processing failed: " + e.getMessage();
        exchange.sendResponseHeaders(400, response.length());
        exchange.getResponseBody().write(response.getBytes());
      } finally {
        exchange.getResponseBody().close();
      }
    } else {
      exchange.sendResponseHeaders(405, -1); // Method Not Allowed
    }
  }

}
