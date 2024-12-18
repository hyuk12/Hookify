package com.hookify.handlers.discord.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscordNotifier {
  private static final OkHttpClient client = new OkHttpClient();
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger logger = LoggerFactory.getLogger(DiscordNotifier.class);

  public static void sendMessage(String webhookUrl, DiscordMessage message) {
    try {
      String jsonPayload = objectMapper.writeValueAsString(message);

      RequestBody body = RequestBody.create(
          jsonPayload, MediaType.get("application/json; charset=utf-8")
      );

      Request request = new Request.Builder()
          .url(webhookUrl)
          .post(body)
          .build();

      try (Response response = client.newCall(request).execute()) {
        if (response.isSuccessful()) {
          logger.info("Message sent to Discord successfully!");
        } else {
          logger.warn("Failed to send message: {}", response.body().string());
        }
      }
    } catch (Exception e) {
      logger.error("Failed to send message to Discord: ", e);
    }
  }
}
