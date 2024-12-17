package com.hookify.handlers.github.processor;

import com.hookify.core.PostProcessor;

public class GitHubProcessor implements PostProcessor {

  @Override
  public void process(String payload) {
    System.out.println("Processing GitHub payload: " + payload);
  }
}
