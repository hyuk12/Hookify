package com.hookify.examples.slack;

import com.hookify.handlers.slack.SlackExtendedHandler;
import com.hookify.handlers.slack.SlackWebhookHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SlackTest {
  public static void main(String[] args) throws IOException {
// 환경 변수에서 Slack 봇 토큰 가져오기
    String slackBotToken = System.getenv("SLACK_BOT_TOKEN");
    if (slackBotToken == null || slackBotToken.isEmpty()) {
      throw new IllegalStateException("SLACK_BOT_TOKEN 환경 변수가 설정되지 않았습니다.");
    }

    // Slack 핸들러 초기화
    SlackExtendedHandler slackExtendedHandler = new SlackExtendedHandler(slackBotToken);

    // HTTP 서버 생성
    HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
    server.createContext("/slack/events", exchange -> handleRequest(exchange, slackExtendedHandler));
    server.start();

    System.out.println("Slack Webhook 서버가 8080 포트에서 실행 중...");
  }

  private static void handleRequest(HttpExchange exchange, SlackExtendedHandler handler) throws IOException {
    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
      // 요청 본문 읽기
      byte[] requestBody = exchange.getRequestBody().readAllBytes();
      String payload = new String(requestBody);
      System.out.println("Payload received: " + payload);

      // Slack 검증 요청 처리
      if (payload.contains("\"challenge\"")) {
        // JSON 파싱 (간단한 문자열 처리 예제)
        String challenge = payload.split("\"challenge\":\"")[1].split("\"")[0];
        System.out.println("Challenge received: " + challenge);

        // 응답 전송
        String response = String.format("{\"challenge\":\"%s\"}", challenge);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
        return;
      }

      // Slack 핸들러 처리
      handler.handle(payload);

      // 응답 전송
      String response = "Webhook event received and processed.";
      exchange.sendResponseHeaders(200, response.length());
      OutputStream os = exchange.getResponseBody();
      os.write(response.getBytes());
      os.close();
    } else {
      // GET 요청 또는 기타 요청에 대한 응답 처리
      exchange.sendResponseHeaders(405, -1); // Method Not Allowed
    }
  }

}
