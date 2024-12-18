package com.hookify.mapper.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hookify.core.enums.EventType;
import com.hookify.handlers.discord.message.DiscordMessage;
import java.util.List;

public class GitHubDiscordMessageMapper {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static DiscordMessage mapToDiscordMessage(String eventType, String payload) {
    try {
      JsonNode jsonNode = objectMapper.readTree(payload);
      DiscordMessage message = new DiscordMessage();

      message.setContent("GitHub Webhook Event: " + eventType);
      message.setUsername("GitHub Webhook");
      message.setAvatarUrl(getUserAvatarUrl(eventType, jsonNode)); // 아바타 URL 설정

      DiscordMessage.Embed embed = new DiscordMessage.Embed();
      embed.setTitle("Event: " + eventType);
      embed.setDescription(summarizeEvent(eventType, jsonNode)); // 이벤트 요약
      embed.setColor(getEventColor(eventType)); // 이벤트 색상 설정

      message.setEmbeds(List.of(embed));
      return message;
    } catch (Exception e) {
      throw new RuntimeException("Failed to map payload to DiscordMessage", e);
    }
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
    String before = node.path("before").asText();
    String after = node.path("after").asText();
    String pusher = node.path("pusher").path("name").asText();
    return String.format("Branch: %s\nCommits: %s → %s\nPusher: %s", branch, before, after, pusher);
  }

  private static String summarizePullRequestEvent(JsonNode node) {
    String title = node.path("pull_request").path("title").asText();
    String state = node.path("action").asText();
    String user = node.path("pull_request").path("user").path("login").asText();
    String url = node.path("pull_request").path("html_url").asText();
    return String.format("Title: %s\nState: %s\nUser: %s\nLink: %s", title, state, user, url);
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
