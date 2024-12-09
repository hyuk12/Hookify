package com.hookify.handlers.slack;

public class SlackWebhookPayload {
  private String type;
  private String text;
  private String user;
  private String channel;

  public String getType() {
    return type;
  }

  public String getText() {
    return text;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }
}
