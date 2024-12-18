package com.hookify.handlers.discord.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
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
      if (Objects.isNull(message) || (message.getContent() == null && message.getEmbeds().isEmpty())) {
        logger.warn("Message is empty. Nothing to send.");
        return;
      }

      String jsonPayload = objectMapper.writeValueAsString(message);
      logger.debug("Generated JSON Payload: {}", jsonPayload);

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
          String retryAfter = response.header("Retry-After");
          if (retryAfter != null) {
            int delay = Integer.parseInt(retryAfter) * 1000; // ms 단위로 변환
            logger.warn("Rate limited. Retrying after {} ms", delay);
            Thread.sleep(delay);
            sendMessage(webhookUrl, message); // 재시도
          } else {
            logger.error("Failed to send message to Discord: {} - Payload: {}",
                response.body().string(), jsonPayload);
          }
        }
      }

      // 요청 간 딜레이 추가
      Thread.sleep(500); // 500ms 지연
    } catch (Exception e) {
      logger.error("Failed to send message to Discord: ", e);
    }
  }
}
