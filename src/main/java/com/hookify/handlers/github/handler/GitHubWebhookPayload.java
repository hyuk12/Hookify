package com.hookify.handlers.github.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GitHubWebhookPayload {
  private String action;
  private String ref;
  private String before;
  private String after;
  private Repository repository;
  private Pusher pusher;
  private Sender sender;
  private List<Commit> commits;
  private Commit headCommit;
  private PullRequest pullRequest;

  public static class Repository {
    private String name;
    private String fullName;
    private Owner owner;

    public static class Owner {
      private String name;
      private String email;
      private String login;

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getEmail() {
        return email;
      }

      public void setEmail(String email) {
        this.email = email;
      }

      public String getLogin() {
        return login;
      }

      public void setLogin(String login) {
        this.login = login;
      }
    }

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

    public Owner getOwner() {
      return owner;
    }

    public void setOwner(Owner owner) {
      this.owner = owner;
    }
  }

  public static class Pusher {
    private String name;
    private String email;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }
  }

  public static class Sender {
    private String login;
    private String avatarUrl;

    public String getLogin() {
      return login;
    }

    public void setLogin(String login) {
      this.login = login;
    }

    public String getAvatarUrl() {
      return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
      this.avatarUrl = avatarUrl;
    }
  }

  public static class Commit {
    private String id;
    private String message;
    private Author author;
    private Committer committer;
    private List<String> added;
    private List<String> removed;
    private List<String> modified;

    public static class Author {
      private String name;
      private String email;

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getEmail() {
        return email;
      }

      public void setEmail(String email) {
        this.email = email;
      }
    }

    public static class Committer {
      private String name;
      private String email;

      public String getName() {
        return name;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getEmail() {
        return email;
      }

      public void setEmail(String email) {
        this.email = email;
      }
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public Author getAuthor() {
      return author;
    }

    public void setAuthor(Author author) {
      this.author = author;
    }

    public Committer getCommitter() {
      return committer;
    }

    public void setCommitter(Committer committer) {
      this.committer = committer;
    }

    public List<String> getAdded() {
      return added;
    }

    public void setAdded(List<String> added) {
      this.added = added;
    }

    public List<String> getRemoved() {
      return removed;
    }

    public void setRemoved(List<String> removed) {
      this.removed = removed;
    }

    public List<String> getModified() {
      return modified;
    }

    public void setModified(List<String> modified) {
      this.modified = modified;
    }
  }

  public static class PullRequest {
    private String title;
    private String body;
    private String state;

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
  }

  // Getters and setters for the root class fields
  public String getRef() {
    return ref;
  }

  public void setRef(String ref) {
    this.ref = ref;
  }

  public String getBefore() {
    return before;
  }

  public void setBefore(String before) {
    this.before = before;
  }

  public String getAfter() {
    return after;
  }

  public void setAfter(String after) {
    this.after = after;
  }

  public Repository getRepository() {
    return repository;
  }

  public void setRepository(Repository repository) {
    this.repository = repository;
  }

  public Pusher getPusher() {
    return pusher;
  }

  public void setPusher(Pusher pusher) {
    this.pusher = pusher;
  }

  public Sender getSender() {
    return sender;
  }

  public void setSender(Sender sender) {
    this.sender = sender;
  }

  public List<Commit> getCommits() {
    return commits;
  }

  public void setCommits(List<Commit> commits) {
    this.commits = commits;
  }

  public Commit getHeadCommit() {
    return headCommit;
  }

  public void setHeadCommit(Commit headCommit) {
    this.headCommit = headCommit;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public PullRequest getPullRequest() {
    return pullRequest;
  }

  public void setPullRequest(
      PullRequest pullRequest) {
    this.pullRequest = pullRequest;
  }

  @Override
  public String toString() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      // JSON 직렬화
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      // 변환 실패 시 기본 Object의 toString 반환
      return super.toString();
    }
  }
}
