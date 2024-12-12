package com.hookify.handlers.github.handler;

public class GitHubWebhookPayload {
  private String action;
  private Pusher pusher;
  private PullRequest pullRequest;
  private Repository repository;

  public static class Repository {
    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public static class Pusher {
    private String name;
    private String fullName;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getFullName() {
      return fullName;
    }

    public void setFullName(String fullName) {
      this.fullName = fullName;
    }
  }

  public static class PullRequest {
    private String title;
    private String body;
    private String state;
    private String htmlUrl;

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getBody() {
      return body;
    }

    public void setBody(String body) {
      this.body = body;
    }

    public String getState() {
      return state;
    }

    public void setState(String state) {
      this.state = state;
    }

    public String getHtmlUrl() {
      return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
      this.htmlUrl = htmlUrl;
    }
  }

  public Repository getRepository() {
    return repository;
  }

  public void setRepository(Repository repository) {
    this.repository = repository;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Pusher getPusher() {
    return pusher;
  }

  public void setPusher(Pusher pusher) {
    this.pusher = pusher;
  }

  public PullRequest getPullRequest() {
    return pullRequest;
  }

  public void setPullRequest(PullRequest pullRequest) {
    this.pullRequest = pullRequest;
  }
}
