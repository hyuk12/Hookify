package com.hookify.core;

import com.hookify.core.handler.WebhookHandler;
import com.hookify.core.validator.WebhookValidator;
import java.util.HashMap;
import java.util.Map;

public class WebhookProcessor {
  private final Map<String, WebhookHandler> handlers = new HashMap<>();
  private final Map<String, WebhookValidator> validators = new HashMap<>();
  private final Map<String, PostProcessor> postProcessors = new HashMap<>();

  // 핸들러 등록
  public void registerHandler(String event, WebhookHandler handler) {
    handlers.put(event, handler);
  }

  // 검증기 등록
  public void registerValidator(String event, WebhookValidator validator) {
    validators.put(event, validator);
  }

  // 후속 작업(PostProcessor)
  public void registerPostProcessor(String event, PostProcessor postProcessor) {
    postProcessors.put(event, postProcessor);
  }
//
//  // Webhook 요청 처리
//  public void process(String event, String signature, String timestamp, String payload) {
//    // 검증 단계
//    WebhookValidator validator = validators.get(event);
//    if (validator != null && !validator.validate(signature, timestamp, payload)) {
//      System.out.println("Validation failed for event: " + event);
//      return;
//    }
//
//    // 핸들러 실행
//    WebhookHandler handler = handlers.get(event);
//    if (handler != null) {
//      handler.handle(payload);
//    } else {
//      System.out.println("No handler found for event: " + event);
//    }
//
//    // 후속 작업 실행
//    PostProcessor postProcessor = postProcessors.get(event);
//    if (postProcessor != null) {
//      postProcessor.process(payload);
//    }
//  }

//  public void processWithRetry(String event, String signature, String timestamp, String payload, RetryPolicy retryPolicy) {
//    int attempts = 0;
//    while (attempts < retryPolicy.maxRetries()) {
//      try {
//        process(event, signature, timestamp, payload);
//        return;
//      } catch (Exception e) {
//        attempts++;
//        System.out.println("Retry attempt " + attempts + " for event: " + event);
//        try {
//          Thread.sleep(retryPolicy.retryDelay());
//        } catch (InterruptedException interruptedException) {
//          Thread.currentThread().interrupt();
//          throw new RuntimeException("Retry interrupted", interruptedException);
//        }
//      }
//    }
//    System.out.println("All retry attempts failed for event: " + event);
//  }

  public void addCustomHandler(String event, WebhookHandler handler) {
    handlers.put(event, handler);
  }


}