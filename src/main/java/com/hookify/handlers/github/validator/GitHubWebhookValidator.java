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
      // GitHub Webhook에서 타임스탬프는 전달되지 않음
      String data = "sha256=" + payload;
      Mac mac = Mac.getInstance(HMAC_ALGORITHM);
      mac.init(new SecretKeySpec(secret.getBytes(), HMAC_ALGORITHM));
      String computedSignature = "sha256=" + bytesToHex(mac.doFinal(payload.getBytes()));

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
