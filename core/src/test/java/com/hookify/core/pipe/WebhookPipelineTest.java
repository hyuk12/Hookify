package com.hookify.core.pipe;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.hookify.handlers.github.pipe.GitHubWebhookPipeline;
import com.hookify.util.SignatureUtils;
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

  @Test
  void testWebhookPipelineExecution_withValidSignature() {
    String secret = "test-secret";
    String validSignature = "sha256=" + secret;
    String payload = "{ \"test\": \"payload\" }";

    WebhookPipeline pipeline = GitHubWebhookPipeline.create(secret);
    String eventType = "push";

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