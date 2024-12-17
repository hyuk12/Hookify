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
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print 활성화
    File directory = new File(LOG_DIRECTORY);
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }

  public void log(String eventType, String payload, boolean success) {
    Map<String, Object> logData = new HashMap<>();
    logData.put("eventType", eventType);
    logData.put("payload", parseNestedJson(payload));
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

  private Object parseNestedJson(String payload) {
    try {
      // 1단계: payload를 JSON 객체로 파싱
      Object json = objectMapper.readValue(payload, Object.class);

      // 2단계: 이중 JSON인지 확인
      if (json instanceof String) {
        String nestedJson = (String) json;
        return objectMapper.readValue(nestedJson, Object.class); // 이중 JSON 파싱
      }
      return json; // 이미 파싱된 JSON 반환
    } catch (IOException e) {
      System.out.println("Failed to parse nested JSON: " + e.getMessage());
      return payload; // 파싱 실패 시 원본 반환
    }
  }
}