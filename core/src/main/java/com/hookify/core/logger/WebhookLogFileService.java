package com.hookify.core.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hookify.core.enums.EventType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebhookLogFileService {
  private static final String LOG_DIRECTORY = "logs/webhook_logs/";
  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger logger = LoggerFactory.getLogger(WebhookLogFileService.class);

  public WebhookLogFileService() {
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print 활성화
    File directory = new File(LOG_DIRECTORY);
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }

  public void successLog(String eventType, String payload) {
    String fileName = LOG_DIRECTORY + eventType + "_" + EventType.SUCCESS + "_" + System.currentTimeMillis() + ".json";
    saveLog(fileName, eventType, payload, String.valueOf(EventType.SUCCESS));
  }

  public void failedLog(String eventType, String payload) {
    String fileName = LOG_DIRECTORY + eventType + "_" + EventType.FAILED + "_" + System.currentTimeMillis() + ".json";
    saveLog(fileName, eventType, payload, String.valueOf(EventType.FAILED));
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
      logger.info("Retry Log saved to: {}", fileName);
    } catch (IOException e) {
      logger.error("Failed to save retry log: {}", e.getMessage());
    }
  }

  private void saveLog(String fileName, String eventType, String payload, String status) {
    Map<String, Object> logData = new HashMap<>();
    logData.put("eventType", eventType);
    logData.put("payload", payload);
    logData.put("status", status);
    logData.put("timestamp", LocalDateTime.now().toString());

    try (FileWriter writer = new FileWriter(fileName)) {
      objectMapper.writeValue(writer, logData);
      logger.info("Retry Log saved to: {}", fileName);
    } catch (IOException e) {
      logger.error("Failed to save retry log: {}", e.getMessage());
    }
  }

  private Object parseNestedJson(String payload) {
    try {
      Object json = objectMapper.readValue(payload, Object.class);
      if (json instanceof String nestedJson) {
        return objectMapper.readValue(nestedJson, Object.class);
      }
      return json;
    } catch (IOException e) {
      logger.error("Failed to parse nested JSON: {}", e.getMessage());
      return payload;
    }
  }
}