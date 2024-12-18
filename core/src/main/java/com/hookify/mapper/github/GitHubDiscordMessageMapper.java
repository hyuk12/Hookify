package com.hookify.mapper.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hookify.core.enums.EventType;
import com.hookify.handlers.discord.message.DiscordMessage;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public class GitHubDiscordMessageMapper {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final Set<String> processedEvents = ConcurrentHashMap.newKeySet();
  private static Map<String, Map<String, String>> eventFilters = new ConcurrentHashMap<>(); // 필터 조건 주입

  public static void setEventFilters(Map<String, Map<String, String>> filters) {
    eventFilters = filters;
  }

  public static DiscordMessage mapToDiscordMessage(String eventType, String payload) {
    try {
      JsonNode jsonNode = objectMapper.readTree(payload);
      String eventId = getEventId(eventType, jsonNode); // 고유 이벤트 ID 가져오기

      // 이벤트 필터링: 외부 조건 확인
      if (shouldFilterEvent(eventType, jsonNode)) {
        return null; // 필터링된 이벤트는 무시
      }

      // 중복 확인
      if (isDuplicate(eventType, jsonNode)) {
        return null; // 중복 이벤트는 무시
      }

      return buildDiscordMessage(eventType,
          jsonNode);
    } catch (Exception e) {
      throw new RuntimeException("Failed to map payload to DiscordMessage", e);
    }
  }

  private static DiscordMessage buildDiscordMessage(String eventType, JsonNode jsonNode) {
    String emoji = getEmoji(jsonNode);
    DiscordMessage message = new DiscordMessage();
    message.setContent(emoji + " **GitHub Webhook Event: " + eventType + "**");
    message.setUsername("GitHub Webhook");
    message.setAvatarUrl(getUserAvatarUrl(eventType, jsonNode));

    DiscordMessage.Embed embed = new DiscordMessage.Embed();
    embed.setTitle(emoji + " **Event: " + eventType + "**");
    embed.setDescription(summarizeEvent(eventType, jsonNode));
    embed.setColor(getEventColor(eventType));

    message.setEmbeds(List.of(embed));
    return message;
  }

  private static boolean shouldFilterEvent(String eventType, JsonNode jsonNode) {
    if (eventFilters.containsKey(eventType)) {
      Map<String, String> conditions = eventFilters.get(eventType);
      for (Map.Entry<String, String> entry : conditions.entrySet()) {
        String key = entry.getKey();
        String expectedValue = entry.getValue();
        String actualValue = jsonNode.path(key).asText();

        if (!"*".equals(expectedValue) && !expectedValue.equalsIgnoreCase(actualValue)) {
          return true; // 조건에 맞지 않으면 필터링
        }
      }
    }
    return false; // 필터링하지 않음
  }


  private static String getEmoji(JsonNode payload) {
    String status = payload.path("action").asText();
    return switch (status) {
      case "opened", "synchronize", "reopened" -> "🔵";
      case "closed" -> "🔴";
      case "created", "edited" -> "📝";
      case "published" -> "📦";
      case "started" -> "🏃";
      case "deleted" -> "🗑️";
      case "completed" -> "✅";
      case "failure" -> "⚠️";
      case "in_progress" -> "⏳";
      default -> "🛠️";
    };
  }

  private static String getEventId(String eventType, JsonNode node) {
    return switch (eventType) {
      case "push" -> node.path("after").asText();
      case "workflow_run" -> node.path("workflow_run").path("id").asText();
      case "check_run" -> String.format("%s-%s-%s",
          node.path("check_run").path("head_sha").asText(),
          node.path("check_run").path("name").asText(),
          node.path("check_run").path("status").asText()
      );
      case "workflow_job" -> String.format("%s-%s-%s",
          node.path("workflow_job").path("head_sha").asText(),
          node.path("workflow_job").path("name").asText(),
          node.path("workflow_job").path("status").asText()
      );
      case "check_suite" -> String.format("%s-%s-%s",
          node.path("check_suite").path("head_sha").asText(),
          node.path("check_suite").path("status").asText(),
          node.path("check_suite").path("conclusion").asText()
      );
      case "release" -> String.format("%s-%s",
          node.path("release").path("id").asText(),
          node.path("release").path("tag_name").asText()
      );
      case "pull_request" -> String.format("%s-%s-%s",
          node.path("pull_request").path("id").asText(),
          node.path("pull_request").path("number").asText(),
          node.path("pull_request").path("state").asText()
      );
      case "pull_request_review" -> String.format("%s-%s-%s",
          node.path("pull_request_review").path("id").asText(),
          node.path("pull_request_review").path("pull_request_url").asText(),
          node.path("pull_request_review").path("state").asText()
      );
      case "pull_request_review_comment" -> String.format("%s-%s-%s",
          node.path("pull_request_review_comment").path("id").asText(),
          node.path("pull_request_review_comment").path("pull_request_url").asText(),
          node.path("pull_request_review_comment").path("state").asText()
      );
      case "create", "delete" -> String.format("%s-%s-%s",
          node.path("ref").asText(),
          node.path("ref_type").asText(),
          node.path("sender").path("login").asText()
      );
      default -> String.valueOf(System.currentTimeMillis());
    };
  }

  private static boolean isDuplicate(String eventType, JsonNode payload) {
    String eventId = getEventId(eventType, payload);
    return !processedEvents.add(eventId);
  }

  private static String summarizeEvent(String eventType, JsonNode node) {
    return switch (eventType) {
      case "push" -> summarizePushEvent(node);
      case "pull_request" -> summarizePullRequestEvent(node);
      case "pull_request_review" -> summarizePullRequestReviewEvent(node);
      case "pull_request_review_comment" -> summarizePullRequestReviewCommentEvent(node);
      case "workflow_run" -> summarizeWorkflowEvent(node);
      case "check_suite" -> summarizeCheckSuiteEvent(node);
      case "release" -> summarizeReleaseEvent(node);
      case "delete" -> summarizeDeleteEvent(node);
      case "create" -> summarizeCreateEvent(node);
      default -> "Unhandled event type: " + eventType;
    };
  }

  private static String summarizeCreateEvent(JsonNode node) {
    String ref = node.path("ref").asText(); // 생성된 브랜치나 태그 이름
    String refType = node.path("ref_type").asText(); // ref 타입: branch, tag 등
    String actor = node.path("sender").path("login").asText(); // 이벤트를 발생시킨 사용자

    return String.format("**Created**: `%s`%n**Type**: `%s`%n**Actor**: `%s`", ref, refType, actor);
  }

  private static String summarizeDeleteEvent(JsonNode node) {
    String ref = node.path("ref").asText(); // 삭제된 브랜치나 태그 이름
    String refType = node.path("ref_type").asText(); // ref 타입: branch, tag 등
    String actor = node.path("sender").path("login").asText(); // 이벤트를 발생시킨 사용자

    return String.format("**Deleted**: `%s`%n**Type**: `%s`%n**Actor**: `%s`", ref, refType, actor);
  }

  private static String summarizePushEvent(JsonNode node) {
    String branch = node.path("ref").asText();
    String after = node.path("after").asText();
    String pusher = node.path("pusher").path("name").asText();
    return String.format("**Branch**: %s\n**Commit**: %s\n**Pusher**: %s", branch, after, pusher);
  }

  private static String summarizePullRequestEvent(JsonNode node) {
    String title = node.path("pull_request").path("title").asText();
    String state = node.path("action").asText();
    String user = node.path("pull_request").path("user").path("login").asText();
    return String.format("**Title**: %s\n**State**: %s\n**User**: %s", title, state, user);
  }

  private static String summarizeWorkflowEvent(JsonNode node) {
    String name = node.path("workflow_run").path("name").asText();
    String status = node.path("workflow_run").path("status").asText();
    String conclusion = node.path("workflow_run").path("conclusion").asText();
    String actor = node.path("workflow_run").path("actor").path("login").asText();
    return String.format("**Workflow**: %s\n**Status**: %s\n**Conclusion**: %s\n**Actor**: %s", name, status, conclusion, actor);
  }

  private static String summarizeCheckSuiteEvent(JsonNode node) {
    String status = node.path("check_suite").path("status").asText();
    String conclusion = node.path("check_suite").path("conclusion").asText();
    String headSha = node.path("check_suite").path("head_sha").asText(); // head_sha 필드 수정
    return String.format("**Status**: %s%n**Conclusion**: %s%n**Head SHA**: %s", status, conclusion, headSha);
  }

  private static String summarizeReleaseEvent(JsonNode node) {
    String name = node.path("release").path("name").asText();
    String tagName = node.path("release").path("tag_name").asText();
    String author = node.path("release").path("author").path("login").asText();
    return String.format("**Release**: %s%n**Tag**: %s%n**Author**: %s", name, tagName, author);
  }

  private static String summarizePullRequestReviewEvent(JsonNode node) {
    String state = node.path("pull_request_review").path("state").asText(); // 리뷰 상태
    String user = node.path("pull_request_review").path("user").path("login").asText(); // 작성자
    String url = node.path("pull_request_review").path("html_url").asText(); // 리뷰 URL

    return String.format(
        "**Review State**: %s%n**Author**: %s%n[Review Link](%s)",
        state, user, url
    );
  }

  private static String summarizePullRequestReviewCommentEvent(JsonNode node) {
    String body = node.path("pull_request_review_comment").path("body").asText(); // 댓글 내용
    String user = node.path("pull_request_review_comment").path("user").path("login").asText(); // 작성자
    String url = node.path("pull_request_review_comment").path("html_url").asText(); // 댓글 URL

    return String.format(
        "**Comment**: %s%n**Author**: %s%n[Comment Link](%s)",
        body, user, url
    );
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
      case "check_suite" -> 0xFFA500; // Orange
      case "release" -> 0x800080; // Purple
      case "delete" -> 0xFF0000; // Red
      case "create" -> 0x008000; // Green
      default -> 0x808080; // Gray
    };
  }


}
