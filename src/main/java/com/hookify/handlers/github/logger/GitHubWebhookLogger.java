package com.hookify.handlers.github.logger;

import com.hookify.handlers.github.handler.GitHubWebhookPayload;
import com.hookify.util.JsonUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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

  public void logPushEvent(GitHubWebhookPayload payload) {
    String repositoryName = payload.getRepository().getName();
    String pusherName = payload.getPusher().getName();

    // 커밋 요약 정보만 추출
    String commitSummary = payload.getCommits().stream()
        .map(commit -> String.format("Commit ID: %s, Message: %s, Modified: %s",
            commit.getId(),
            commit.getMessage(),
            String.join(", ", commit.getModified())))
        .collect(Collectors.joining("\n"));

    // 로그 메시지 생성
    String logMessage = String.format(
        "Repository: %s%nPusher: %s%nCommits:%n%s%n",
        repositoryName, pusherName, commitSummary);

    logToFile("push.log", logMessage);
  }

  public void logPullRequestEvent(GitHubWebhookPayload payload) {
    String repositoryName = payload.getRepository().getName();
    String action = payload.getAction();
    String prTitle = payload.getPullRequest().getTitle();
    String prBody = payload.getPullRequest().getBody();

    // 로그 메시지 생성
    String logMessage = String.format(
        "Repository: %s%nAction: %s%nPR Title: %s%nPR Description: %s%n",
        repositoryName, action, prTitle, prBody != null ? prBody : "No description");

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
