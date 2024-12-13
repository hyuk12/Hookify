package com.hookify.handlers.github.validator;

import static com.hookify.util.StringUtils.bytesToHex;

import com.hookify.core.WebhookValidator;
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
      String data = "sha256=" + timestamp + ":" + payload;
      Mac mac = Mac.getInstance(HMAC_ALGORITHM);
      mac.init(new SecretKeySpec(secret.getBytes(), HMAC_ALGORITHM));
      String computedSignature = "sha256=" + bytesToHex(mac.doFinal(data.getBytes()));

      return computedSignature.equals(signature);
    } catch (Exception e) {
      return false;
    }
  }
}
