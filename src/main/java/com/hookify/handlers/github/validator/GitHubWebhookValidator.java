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
      long requestTime = Long.parseLong(timestamp);
      long currentTime = System.currentTimeMillis() / 1000;
      if (Math.abs(currentTime - requestTime) > 300) { // 5분 이상 차이
        System.out.println("Timestamp validation failed.");
        return false;
      }

      String data = "sha256=" + timestamp + ":" + payload;
      Mac mac = Mac.getInstance(HMAC_ALGORITHM);
      mac.init(new SecretKeySpec(secret.getBytes(), HMAC_ALGORITHM));
      String computedSignature = "sha256=" + bytesToHex(mac.doFinal(data.getBytes()));

      if (!computedSignature.equals(signature)) {
        System.out.println("Signature validation failed.");
        return false;
      }
      return true;
    } catch (Exception e) {
      System.out.println("Validation error: " + e.getMessage());
      return false;
    }
  }
}
