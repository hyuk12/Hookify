package com.hookify.mapper.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hookify.core.enums.EventType;
import com.hookify.handlers.discord.message.DiscordMessage;
import java.util.List;

public class GitHubDiscordMessageMapper {
  public static DiscordMessage mapToDiscordMessage(String eventType, String payload) {
    DiscordMessage message = new DiscordMessage();
    message.setContent("GitHub Webhook Event: " + eventType);
    message.setUsername("GitHub Webhook");

    // 이벤트별 데이터 요약
    DiscordMessage.Embed embed = new DiscordMessage.Embed();
    embed.setTitle("Event: " + eventType);
    embed.setDescription(getSummary(eventType, payload));
    embed.setColor(getEventColor(eventType));

    message.setEmbeds(List.of(embed));
    return message;
  }

  private static String getSummary(String eventType, String payload) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jsonNode = mapper.readTree(payload);

      return switch (eventType) {
        case "push" -> summarizePushEvent(jsonNode);
        case "pull_request" -> summarizePullRequestEvent(jsonNode);
        case "workflow_run" -> summarizeWorkflowEvent(jsonNode);
        default -> "Unhandled event type: " + eventType;
      };
    } catch (Exception e) {
      return "Failed to parse payload.";
    }
  }

  private static String summarizePushEvent(JsonNode node) {
    String branch = node.path("ref").asText();
    String before = node.path("before").asText();
    String after = node.path("after").asText();
    return String.format("Branch: %s\nCommits: %s → %s", branch, before, after);
  }

  private static String summarizePullRequestEvent(JsonNode node) {
    String title = node.path("pull_request").path("title").asText();
    String state = node.path("action").asText();
    String url = node.path("pull_request").path("html_url").asText();
    return String.format("Title: %s\nState: %s\nLink: %s", title, state, url);
  }

  private static String summarizeWorkflowEvent(JsonNode node) {
    String name = node.path("workflow_run").path("name").asText();
    String status = node.path("workflow_run").path("status").asText();
    String conclusion = node.path("workflow_run").path("conclusion").asText();
    return String.format("Workflow: %s\nStatus: %s\nConclusion: %s", name, status, conclusion);
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
