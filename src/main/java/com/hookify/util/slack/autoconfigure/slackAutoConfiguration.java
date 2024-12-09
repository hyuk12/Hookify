package com.hookify.util.slack.autoconfigure;

import com.hookify.util.slack.SlackApiClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "slack.bot.token")
public class slackAutoConfiguration {

  @Bean
  public SlackApiClient slackApiClient() {
    String token = System.getenv("SLACK_BOT_TOKEN");
    return new SlackApiClient(token);
  }
}
