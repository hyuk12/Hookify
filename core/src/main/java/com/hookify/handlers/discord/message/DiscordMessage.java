package com.hookify.handlers.discord.message;

import java.util.List;
import lombok.Data;


public class DiscordMessage {
  private String content; // 일반 텍스트 메시지 내용
  private String username; // 메시지 보낸 사용자 이름(옵션)
  private String avatarUrl; // 사용자 아바타 이미지 URL(옵션)
  private List<Embed> embeds; // 추가적인 콘텐츠를 위한 Embed 객체 목록(옵션)

  public static class Embed {
    private String title;
    private String description;
    private int color;
    private List<Field> fields;

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public int getColor() {
      return color;
    }

    public void setColor(int color) {
      this.color = color;
    }

    public List<Field> getFields() {
      return fields;
    }

    public void setFields(
        List<Field> fields) {
      this.fields = fields;
    }

    public static class Field {
      private String name;
      private String value;

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getValue() {
        return value;
      }

      public void setValue(String value) {
        this.value = value;
      }
    }
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public List<Embed> getEmbeds() {
    return embeds;
  }

  public void setEmbeds(List<Embed> embeds) {
    this.embeds = embeds;
  }
}
