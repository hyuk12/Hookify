package com.hookify.core.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    File directory = new File(LOG_DIRECTORY);
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }

  public void log(String eventType, String payload, boolean success) {
    Map<String, Object> logData = new HashMap<>();
    logData.put("eventType", eventType);
    logData.put("payload", payload);
    logData.put("success", success);
    logData.put("timestamp", LocalDateTime.now().toString());

    String fileName = LOG_DIRECTORY + eventType + "_" + System.currentTimeMillis() + ".json";

    try (FileWriter writer = new FileWriter(fileName)) {
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, logData);
      System.out.println("Log saved to: " + fileName);
    } catch (IOException e) {
      System.out.println("Failed to save log: " + e.getMessage());
    }
  }
}
