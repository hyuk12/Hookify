package com.hookify.core.pipe;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.hookify.handlers.github.pipe.GitHubWebhookPipeline;
import com.hookify.util.SignatureUtils;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;

class WebhookPipelineTest {

  @Test
  void testWebhookPipelineExecution_withInvalidSignature() {
    // Arrange
    String secret = "test-secret";
    String invalidSignature = "sha256=invalid";
    String payload = "{ \"test\": \"payload\" }";

    WebhookPipeline pipeline = GitHubWebhookPipeline.create(secret);
    String eventType = "push";

    // Act & Assert
    IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
      pipeline.execute(eventType, invalidSignature, null, payload);
    });

    assertTrue(exception.getMessage().contains("Validation failed after"));
  }

  private String generateHmacSHA256(String data, String secret) throws Exception {
    Mac hmac = Mac.getInstance("HmacSHA256");
    SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    hmac.init(secretKeySpec);
    byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

    StringBuilder hexString = new StringBuilder();
    for (byte b : hash) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) hexString.append('0');
      hexString.append(hex);
    }
    return "sha256=" + hexString.toString();
  }

  @Test
  void testWebhookPipelineExecution_withValidSignature() throws Exception {
    // Arrange
    String secret = "test-secret";
    String payload = "{ \"test\": \"payload\" }";
    String validSignature = generateHmacSHA256(payload, secret); // 올바른 서명 생성

    WebhookPipeline pipeline = GitHubWebhookPipeline.create(secret);
    String eventType = "push";

    // Act & Assert
    assertDoesNotThrow(() -> pipeline.execute(eventType, validSignature, null, payload));
  }

  @Test
  void testWebhookPipelineExecution_withMockedDependencies() {
    // Arrange
    WebhookPipeline pipeline = mock(WebhookPipeline.class);
    String eventType = "push";
    String signature = "sha256=test-signature";
    String payload = "{ \"test\": \"payload\" }";

    // Mocking
    doNothing().when(pipeline).execute(eventType, signature, null, payload);

    // Act & Verify
    assertDoesNotThrow(() -> pipeline.execute(eventType, signature, null, payload));
    verify(pipeline, times(1)).execute(eventType, signature, null, payload);
  }
}