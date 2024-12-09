package com.hookify.handlers.slack.validator;

import static com.hookify.util.StringUtils.bytesToHex;

import com.hookify.core.WebhookValidator;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SlackWebhookValidator implements WebhookValidator {
  private final String signingSecret;

  public SlackWebhookValidator(String signingSecret) {
    this.signingSecret = signingSecret;
  }

  @Override
  public boolean validate(String signature, String timestamp, String payload) {
    try {
      // Slack 검증 문자열 생성
      String baseString = "v0:" + timestamp + ":" + payload;

      // HMAC-SHA256 생성
      Mac hmac = Mac.getInstance("HmacSHA256");
      hmac.init(new SecretKeySpec(signingSecret.getBytes(), "HmacSHA256"));
      byte[] hash = hmac.doFinal(baseString.getBytes());

      // Slack 서명과 비교
      String computedSignature = "v0=" + bytesToHex(hash);

    }catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new RuntimeException(e);
    }
    return false;
  }
}
