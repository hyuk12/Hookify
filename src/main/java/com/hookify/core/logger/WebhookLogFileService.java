package com.hookify.core.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class WebhookLogFileService {
  private static final String LOG_DIRECTORY = "logs/webhook_logs/";
  private final ObjectMapper objectMapper = new ObjectMapper();

  public WebhookLogFileService() {
    // ObjectMapper 설정: Pretty Print 활성화
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

    File directory = new File(LOG_DIRECTORY);
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }

  public void log(String eventType, String payload, boolean success) {
    Map<String, Object> logData = new HashMap<>();
    logData.put("eventType", eventType);
    logData.put("payload", prettyPrintPayload(payload)); // Pretty print payload
    logData.put("success", success);
    logData.put("timestamp", LocalDateTime.now().toString());

    String fileName = LOG_DIRECTORY + eventType + "_" + System.currentTimeMillis() + ".json";

    try (FileWriter writer = new FileWriter(fileName)) {
      objectMapper.writeValue(writer, logData);
      System.out.println("Log saved to: " + fileName);
    } catch (IOException e) {
      System.out.println("Failed to save log: " + e.getMessage());
    }
  }

  // 이중 JSON 문자열을 처리하는 메서드
  private String prettyPrintPayload(String payload) {
    try {
      String cleanedPayload = payload;
      while (true) {
        // JSON 형식으로 파싱 시도
        Object json = objectMapper.readValue(cleanedPayload, Object.class);
        // 파싱이 성공하면 Pretty Print로 변환
        cleanedPayload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
      }
    } catch (IOException e) {
      // 파싱 실패하면 더 이상 이중 JSON이 아니라고 판단하고 반환
      return payload;
    }
  }
}
