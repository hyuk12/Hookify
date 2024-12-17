package com.hookify.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtils {
  private static final ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty Print 설정
  }

  public static <T> T fromJson(String json, Class<T> clazz) {
    try {
      return mapper.readValue(json, clazz);
    } catch (Exception e) {
      throw new RuntimeException("JSON parsing failed", e);
    }
  }

  public static String toJson(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException("JSON serialization failed", e);
    }
  }

  public static String prettyPrint(String json) {
    try {
      Object jsonObject = mapper.readValue(json, Object.class);
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
    } catch (Exception e) {
      throw new RuntimeException("JSON pretty print failed", e);
    }
  }

}
