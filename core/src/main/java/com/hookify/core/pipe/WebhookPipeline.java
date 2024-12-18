package com.hookify.core.pipe;

import com.hookify.core.PostProcessor;
import com.hookify.core.handler.WebhookHandler;
import com.hookify.core.validator.WebhookValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WebhookPipeline {
  private final List<Consumer<WebhookContext>> steps = new ArrayList<>();
  private final List<PostProcessor> postProcessors = new ArrayList<>(); // 다중 프로세스 추가

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

  public WebhookPipeline validator(WebhookValidator validator) {
    steps.add(context -> validator.validate(context.event, context.signature, context.timestamp, context.payload));
    return this;
  }

  public WebhookPipeline handleEvent(WebhookHandler handler) {
    steps.add(context -> handler.handle(context.event, context.payload)); // 이벤트 타입 추가
    return this;
  }

  public WebhookPipeline addPostProcessor(PostProcessor postProcessor) {
    this.postProcessors.add(postProcessor);
    return this;
  }

  public void execute(String event, String signature, String timestamp, String payload) {
    WebhookContext context = new WebhookContext(event, signature, timestamp, payload);
    for (Consumer<WebhookContext> step : steps) {
      step.accept(context);
    }

    for (PostProcessor postProcessor : postProcessors) {
      postProcessor.process(event, payload);
    }
  }

}
