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

  /**
   * 일반 이벤트 로깅 (성공 또는 실패)
   */
  public void log(String eventType, String payload, boolean success) {
    String status = success ? "success" : "fail";
    String fileName = LOG_DIRECTORY + eventType + "_" + status + "_" + System.currentTimeMillis() + ".json";

    saveLog(fileName, eventType, payload, status);
  }

  /**
   * 재시도 이벤트 로깅
   */
  public void logRetry(String eventType, String payload, int attempt, Exception exception) {
    String fileName = LOG_DIRECTORY + eventType + "_retry_" + attempt + "_" + System.currentTimeMillis() + ".json";

    Map<String, Object> logData = new HashMap<>();
    logData.put("eventType", eventType);
    logData.put("payload", parseNestedJson(payload));
    logData.put("retryAttempt", attempt);
    logData.put("errorMessage", exception.getMessage());
    logData.put("timestamp", LocalDateTime.now().toString());

    try (FileWriter writer = new FileWriter(fileName)) {
      objectMapper.writeValue(writer, logData);
      System.out.println("Retry Log saved to: " + fileName);
    } catch (IOException e) {
      System.out.println("Failed to save retry log: " + e.getMessage());
    }
  }

  private void saveLog(String fileName, String eventType, String payload, String status) {
    Map<String, Object> logData = new HashMap<>();
    logData.put("eventType", eventType);
    logData.put("payload", parseNestedJson(payload));
    logData.put("status", status);
    logData.put("timestamp", LocalDateTime.now().toString());

    try (FileWriter writer = new FileWriter(fileName)) {
      objectMapper.writeValue(writer, logData);
      System.out.println("Log saved to: " + fileName);
    } catch (IOException e) {
      System.out.println("Failed to save log: " + e.getMessage());
    }
  }

  private Object parseNestedJson(String payload) {
    try {
      Object json = objectMapper.readValue(payload, Object.class);
      if (json instanceof String) {
        String nestedJson = (String) json;
        return objectMapper.readValue(nestedJson, Object.class);
      }
      return json;
    } catch (IOException e) {
      System.out.println("Failed to parse nested JSON: " + e.getMessage());
      return payload;
    }
  }
}