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
    // ObjectMapper에 pretty print 설정 추가
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

    File directory = new File(LOG_DIRECTORY);
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }

  public void log(String eventType, String payload, boolean success) {
    Map<String, Object> logData = new HashMap<>();
    logData.put("eventType", eventType);
    logData.put("payload", prettyPrintPayload(payload));
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

  private String prettyPrintPayload(String payload) {
    try {
      // payload가 이중 문자열인지 확인하고 변환
      Object json = objectMapper.readValue(payload, Object.class);
      if (json instanceof String) {
        // 이중 문자열 처리
        json = objectMapper.readValue((String) json, Object.class);
      }
      return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
    } catch (IOException e) {
      // 실패 시 원본 payload 반환
      return payload;
    }
  }

}
