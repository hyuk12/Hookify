package com.hookify.core.pipe;

import com.hookify.core.PostProcessor;
import com.hookify.core.handler.WebhookHandler;
import com.hookify.core.validator.WebhookValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WebhookPipeline {
  private final List<Consumer<WebhookContext>> steps = new ArrayList<>();

  // WebhookContext 클래스 정의
  public static class WebhookContext {
    public final String event;
    public final String signature;
    public final String timestamp;
    public final String payload;

    public WebhookContext(String event, String signature, String timestamp, String payload) {
      this.event = event;
      this.signature = signature;
      this.timestamp = timestamp;
      this.payload = payload;
    }
  }

  // 검증 단계 추가
  public WebhookPipeline validator(WebhookValidator validator) {
    steps.add(context -> {
      if (!validator.validate(context.signature, context.timestamp, context.payload)) {
        throw new IllegalStateException("Validation failed for event: " + context.event);
      }
    });
    return this;
  }

  // 이벤트 처리 핸들러 추가
  public WebhookPipeline handleEvent(WebhookHandler handler) {
    steps.add(context -> handler.handle(context.payload));
    return this;
  }

  // 체인에 후속 작업(PostProcessor) 추가
  public WebhookPipeline postProcess(PostProcessor postProcessor) {
    steps.add(context -> postProcessor.process(context.payload));
    return this;
  }

  // 파이프라인 실행
  public void execute(String event, String signature, String timestamp, String payload) {
    WebhookContext context = new WebhookContext(event, signature, timestamp, payload);
    for (Consumer<WebhookContext> step : steps) {
      step.accept(context);
    }
  }

}
