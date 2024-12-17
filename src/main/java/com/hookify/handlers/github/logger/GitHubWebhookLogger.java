package com.hookify.handlers.github.logger;

import com.hookify.util.JsonUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class GitHubWebhookLogger {

  private static final String LOG_DIRECTORY = "logs/github";

  static {
    try {
      Files.createDirectories(Path.of(LOG_DIRECTORY));
    } catch (IOException e) {
      System.out.println("Failed to create log directory: " + e.getMessage());
    }
  }

  public void logPushEvent(String repositoryName, String pusherName, Object payload) {
    String payloadJson = JsonUtils.toJson(payload); // JSON으로 변환
    logToFile("push.log", String.format("Repository: %s, Pusher: %s, Payload: %s%n",
        repositoryName, pusherName, payloadJson));
  }

  public void logPullRequestEvent(String repositoryName, String action, String prTitle, Object payload) {
    String payloadJson = JsonUtils.toJson(payload); // JSON으로 변환
    logToFile("pull_request.log", String.format("Repository: %s, Action: %s, PR Title: %s, Payload: %s%n",
        repositoryName, action, prTitle, payloadJson));
  }

  private void logToFile(String fileName, String logMessage) {
    try {
      Path logFile = Path.of(LOG_DIRECTORY, fileName);
      Files.writeString(logFile, logMessage, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    } catch (IOException e) {
      System.out.println("Failed to log to file: " + e.getMessage());
    }
  }
}
