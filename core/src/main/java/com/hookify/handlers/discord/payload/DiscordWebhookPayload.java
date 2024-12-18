package com.hookify.handlers.discord.payload;

import java.util.List;
import lombok.Data;

@Data
public class DiscordWebhookPayload {
  private String content; // 일반 텍스트 메시지 내용
  private String username; // 메시지 보낸 사용자 이름(옵션)
  private String avatarUrl; // 사용자 아바타 이미지 URL(옵션)
  private List<Embed> embeds; // 추가적인 콘텐츠를 위한 Embed 객체 목록(옵션)

  @Data
  public static class Embed {
    private String title;
    private String description;
    private Long color;
    private List<Field> fields;

    @Data
    public static class Field {
      private String name;
      private String value;
    }
  }

}
