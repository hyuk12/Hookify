package com.hookify.handlers.github.validator;

import static com.hookify.util.StringUtils.bytesToHex;

import com.hookify.core.validator.WebhookValidator;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class GitHubWebhookValidator implements WebhookValidator {
  private static final String HMAC_ALGORITHM = "HmacSHA256";
  private final String secret;

  public GitHubWebhookValidator(String secret) {
    this.secret = secret;
  }

  @Override
  public boolean validate(String eventType, String signature, String timestamp, String payload) {
    try {
      if (signature == null || payload == null) {
        throw new IllegalArgumentException("Signature or payload cannot be null");
      }

      // HMAC-SHA256 해시 생성
      String computedHash = "sha256=" + generateHmacSHA256(payload, secret);

      // 입력받은 시그니처와 비교
      if (!computedHash.equals(signature)) {
        throw new IllegalStateException("Signature validation failed");
      }

      return true; // 검증 성공
    } catch (Exception e) {
      System.out.println("Signature validation failed: " + e.getMessage());
    }
  }

  private String generateHmacSHA256(String data, String key) throws Exception {
    Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
    SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
    hmac.init(secretKeySpec);
    byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    return bytesToHex(hash);
  }


}
