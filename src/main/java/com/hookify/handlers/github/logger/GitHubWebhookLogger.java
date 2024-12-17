package com.hookify.handlers.github.logger;

import com.hookify.handlers.github.handler.GitHubWebhookPayload;
import com.hookify.handlers.github.handler.GitHubWebhookPayload.Commit;
import com.hookify.util.JsonUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class GitHubWebhookLogger {

  private static final String LOG_DIRECTORY = "logs/github";

  static {
    try {
      Files.createDirectories(Path.of(LOG_DIRECTORY));
    } catch (IOException e) {
      System.out.println("Failed to create log directory: " + e.getMessage());
    }
  }

  public void logPushEvent(String repositoryName, String pusherName, List<Commit> commits) {
    String commitSummary = commits.stream()
        .map(commit -> String.format("ID: %s, Message: %s, Modified: %s",
            commit.getId(),
            commit.getMessage(),
            String.join(", ", commit.getModified())))
        .collect(Collectors.joining("\n"));

    String logMessage = String.format(
        "Repository: %s%nPusher: %s%nCommits:%n%s%n",
        repositoryName, pusherName, commitSummary);

    logToFile("push.log", logMessage);
  }

  public void logPullRequestEvent(String repositoryName, String action, String prTitle) {
    String logMessage = String.format(
        "Repository: %s%nAction: %s%nPR Title: %s%n",
        repositoryName, action, prTitle);

    logToFile("pull_request.log", logMessage);
  }

  private void logToFile(String fileName, String logMessage) {
    try {
      Path logFile = Path.of(LOG_DIRECTORY, fileName);
      Files.writeString(logFile, logMessage + System.lineSeparator(),
          StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    } catch (IOException e) {
      System.out.println("Failed to log to file: " + e.getMessage());
    }
  }
}
