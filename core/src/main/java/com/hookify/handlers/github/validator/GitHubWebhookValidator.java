package com.hookify.handlers.github.validator;

import static com.hookify.util.StringUtils.bytesToHex;

import com.hookify.core.validator.WebhookValidator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class GitHubWebhookValidator implements WebhookValidator {
  private static final String HMAC_ALGORITHM = "HmacSHA256";
  private final String secret;

  public GitHubWebhookValidator(String secret) {
    this.secret = secret;
  }

  @Override
  public boolean validate(String signature, String timestamp, String payload) {
    try {
      if (signature == null || payload == null) {
        throw new IllegalArgumentException("Signature or payload cannot be null");
      }

      // 여기서 secret을 사용한 해시 검증 로직 구현
      String expectedSignature = "sha256=" + secret; // 예시, 실제 해시 구현 필요
      if (!signature.equals(expectedSignature)) {
        throw new IllegalStateException("Signature validation failed");
      }

      return true; // 검증 성공
    } catch (Exception e) {
      System.out.println("Signature validation failed: " + e.getMessage());
      throw e; // 실패 시 예외 던짐
    }
  }
}
