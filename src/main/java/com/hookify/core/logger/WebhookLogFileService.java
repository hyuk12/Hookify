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
      // JSON 파싱이 실패할 때까지 반복해서 이중 문자열을 처리
      String result = payload;
      while (true) {
        try {
          // 현재 문자열을 JSON 객체로 파싱
          Object json = objectMapper.readValue(result, Object.class);
          // 파싱된 JSON 객체를 다시 예쁘게 출력
          result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (IOException e) {
          // 더 이상 JSON이 아니면 최종 result 반환
          return result;
        }
      }
    } catch (Exception e) {
      // 실패 시 원본 payload 반환
      return payload;
    }
  }


}
