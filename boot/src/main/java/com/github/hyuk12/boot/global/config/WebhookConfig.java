package com.github.hyuk12.boot.global.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "webhook")
public class WebhookConfig {
  private List<String> enabledEvents; // 활성화할 이벤트 목록
  private Map<String, Map<String, String>> filters = new HashMap<>(); // 이벤트별 필터링 조건

  public List<String> getEnabledEvents() {
    return enabledEvents;
  }

  public void setEnabledEvents(List<String> enabledEvents) {
    this.enabledEvents = enabledEvents;
  }

  public Map<String, Map<String, String>> getFilters() {
    return filters;
  }

  public void setFilters(Map<String, Map<String, String>> filters) {
    this.filters = filters;
  }
}
