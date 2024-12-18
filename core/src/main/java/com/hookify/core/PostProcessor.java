package com.hookify.core;

@FunctionalInterface
public interface PostProcessor {
  void process(String eventType, String payload);

}
