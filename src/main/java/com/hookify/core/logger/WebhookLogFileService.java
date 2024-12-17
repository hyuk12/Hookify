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

  private String prettyPrintPayload(String payload) {
    try {
      // 첫 번째 파싱 시도
      Object json = objectMapper.readValue(payload, Object.class);

      // 파싱된 JSON을 Pretty Print로 변환
      String prettyPayload = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);

      // 두 번째 파싱 시도 (이중 JSON 검사)
      if (prettyPayload.startsWith("\"") && prettyPayload.endsWith("\"")) {
        // 다시 한 번 JSON으로 파싱
        String unescapedPayload = objectMapper.readValue(prettyPayload, String.class);
        Object nestedJson = objectMapper.readValue(unescapedPayload, Object.class);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(nestedJson);
      }

      return prettyPayload;
    } catch (IOException e) {
      // JSON 파싱 실패 시 원본 반환
      return payload;
    }
  }

}
