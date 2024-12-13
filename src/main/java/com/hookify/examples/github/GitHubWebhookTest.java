package com.hookify.examples.github;

import com.hookify.core.pipe.WebhookPipeline;
import com.hookify.handlers.github.pipe.GitHubWebhookPipeline;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class GitHubWebhookTest {

  public static void main(String[] args) throws IOException {
    // Secret 설정
    String secret = "ACM+ZoVPpAeRzEVTzYfKlxYChb7DroCT11uTXw23to8=";

    // GitHub WebhookPipeline 생성
    WebhookPipeline pipeline = GitHubWebhookPipeline.create(secret);

    // HTTP 서버 초기화
    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
    server.createContext("/github/webhook", exchange -> handleRequest(exchange, pipeline));
    server.start();

    System.out.println("GitHub Webhook 서버가 8080 포트에서 실행 중..");
  }

  private static void handleRequest(HttpExchange exchange, WebhookPipeline pipeline) throws IOException {
    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      byte[] requestBody = exchange.getRequestBody().readAllBytes();
      String payload = new String(requestBody);
      String signature = exchange.getRequestHeaders().getFirst("X-Hub-Signature-256");
      String event = exchange.getRequestHeaders().getFirst("X-GitHub-Event");
      String timestamp = Long.toString(System.currentTimeMillis() / 1000); // 현재 타임스탬프

      System.out.println("Webhook 요청 수신:");
      System.out.println("Event: " + event);
      System.out.println("Signature: " + signature);
      System.out.println("Payload: " + payload);

      try {
        pipeline.execute(event, signature, timestamp, payload);

        // 성공 응답
        String response = "Webhook processed successfully.";
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
      } catch (Exception e) {
        // 실패 응답
        String response = "Webhook processing failed: " + e.getMessage();
        exchange.sendResponseHeaders(400, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
      }
    } else {
      // GET 요청 또는 기타 요청에 대한 응답 처리
      exchange.sendResponseHeaders(405, -1); // Method Not Allowed
    }
  }

}
