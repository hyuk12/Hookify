package com.hookify.handlers.github.validator;

import static com.hookify.util.StringUtils.bytesToHex;

import com.hookify.core.logger.WebhookLogFileService;
import com.hookify.core.validator.WebhookValidator;
import com.hookify.handlers.github.processor.GitHubProcessor;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitHubWebhookValidator implements WebhookValidator {
  private final WebhookLogFileService webhookLogFileService = new WebhookLogFileService();
  private static final String HMAC_ALGORITHM = "HmacSHA256";
  private final String secret;
  private static final Logger logger = LoggerFactory.getLogger(GitHubWebhookValidator.class);

  public GitHubWebhookValidator(String secret) {
    this.secret = secret;
  }

  @Override
  public boolean validate(String eventType, String signature, String timestamp, String payload) {
    if (Objects.isNull(signature) || Objects.isNull(payload)) {
      logger.warn("Signature or payload is null");
      webhookLogFileService.failedLog(eventType, payload);
      return false; // 검증 실패 시 false 반환
    }

    try {
      // HMAC-SHA256 해시 생성
      String computedHash = "sha256=" + generateHmacSHA256(payload, secret);

      // 입력받은 시그니처와 비교
      boolean isValid = computedHash.equals(signature);
      if (!isValid) {
        webhookLogFileService.failedLog(eventType, payload);
        logger.warn("Signature validation failed");
      }
      return isValid; // 검증 결과 반환
    } catch (Exception e) {
      webhookLogFileService.failedLog(eventType, payload);
      logger.error("Signature validation error: {}", e.getMessage());
      return false; // 예외 발생 시 false 반환
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
