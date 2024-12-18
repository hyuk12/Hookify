package com.hookify.mapper.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hookify.core.enums.EventType;
import com.hookify.handlers.discord.message.DiscordMessage;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GitHubDiscordMessageMapper {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final ConcurrentHashMap<String, Boolean> processedEventCache = new ConcurrentHashMap<>();

  public static DiscordMessage mapToDiscordMessage(String eventType, String payload) {
    try {
      JsonNode jsonNode = objectMapper.readTree(payload);
      String eventId = getEventId(eventType, jsonNode); // 고유 이벤트 ID 가져오기

      // 중복 확인
      if (isDuplicateEvent(eventId)) {
        return null; // 중복이면 메시지 생성 X
      }

      DiscordMessage message = new DiscordMessage();
      message.setContent("GitHub Webhook Event: " + eventType);
      message.setUsername("GitHub Webhook");
      message.setAvatarUrl(getUserAvatarUrl(eventType, jsonNode));

      DiscordMessage.Embed embed = new DiscordMessage.Embed();
      embed.setTitle("Event: " + eventType);
      embed.setDescription(summarizeEvent(eventType, jsonNode));
      embed.setColor(getEventColor(eventType));

      message.setEmbeds(List.of(embed));
      return message;
    } catch (Exception e) {
      throw new RuntimeException("Failed to map payload to DiscordMessage", e);
    }
  }

  private static String getEventId(String eventType, JsonNode node) {
    return switch (eventType) {
      case "push" -> node.path("after").asText(); // 커밋 해시 사용
      case "workflow_run" -> node.path("workflow_run").path("id").asText(); // 워크플로우 ID 사용
      case "check_run" -> {
        String checkRunSha = node.path("check_run").path("head_sha").asText();
        String checkRunName = node.path("check_run").path("name").asText();
        String checkRunStatus = node.path("check_run").path("status").asText();
        yield String.format("%s-%s-%s", checkRunSha, checkRunName, checkRunStatus);
      }
      case "workflow_job" -> {
        String jobSha = node.path("workflow_job").path("head_sha").asText();
        String jobName = node.path("workflow_job").path("name").asText();
        String jobStatus = node.path("workflow_job").path("status").asText();
        yield String.format("%s-%s-%s", jobSha, jobName, jobStatus);
      }
      case "pull_request" -> node.path("pull_request").path("id").asText();
      default -> String.valueOf(System.currentTimeMillis());
    };
  }

  private static boolean isDuplicateEvent(String eventId) {
    if (processedEventCache.containsKey(eventId)) {
      return true;
    }
    processedEventCache.put(eventId, true);
    return false;
  }

  private static String summarizeEvent(String eventType, JsonNode node) {
    return switch (eventType) {
      case "push" -> summarizePushEvent(node);
      case "pull_request" -> summarizePullRequestEvent(node);
      case "workflow_run" -> summarizeWorkflowEvent(node);
      default -> "Unhandled event type: " + eventType;
    };
  }

  private static String summarizePushEvent(JsonNode node) {
    String branch = node.path("ref").asText();
    String after = node.path("after").asText();
    String pusher = node.path("pusher").path("name").asText();
    return String.format("Branch: %s\nCommit: %s\nPusher: %s", branch, after, pusher);
  }

  private static String summarizePullRequestEvent(JsonNode node) {
    String title = node.path("pull_request").path("title").asText();
    String state = node.path("action").asText();
    String user = node.path("pull_request").path("user").path("login").asText();
    return String.format("Title: %s\nState: %s\nUser: %s", title, state, user);
  }

  private static String summarizeWorkflowEvent(JsonNode node) {
    String name = node.path("workflow_run").path("name").asText();
    String status = node.path("workflow_run").path("status").asText();
    String conclusion = node.path("workflow_run").path("conclusion").asText();
    String actor = node.path("workflow_run").path("actor").path("login").asText();
    return String.format("Workflow: %s\nStatus: %s\nConclusion: %s\nActor: %s", name, status, conclusion, actor);
  }

  private static String getUserAvatarUrl(String eventType, JsonNode node) {
    return switch (eventType) {
      case "push" -> "https://github.com/" + node.path("pusher").path("name").asText() + ".png";
      case "pull_request" -> node.path("pull_request").path("user").path("avatar_url").asText();
      case "workflow_run" -> node.path("workflow_run").path("actor").path("avatar_url").asText();
      default -> null;
    };
  }

  private static int getEventColor(String eventType) {
    return switch (eventType) {
      case "push" -> 0x00FF00; // Green
      case "pull_request" -> 0xFFD700; // Gold
      case "workflow_run" -> 0x1E90FF; // DodgerBlue
      default -> 0x808080; // Gray
    };
  }


}
