package com.hookify.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SignatureUtils {
  public static String generateSignature(String secret, String payload) throws Exception {
    Mac sha256Hmac = Mac.getInstance("HmacSHA256");
    SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
    sha256Hmac.init(keySpec);

    byte[] hmacBytes = sha256Hmac.doFinal(payload.getBytes());
    return "sha256=" + bytesToHex(hmacBytes);
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder hexString = new StringBuilder();
    for (byte b : bytes) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) hexString.append('0');
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
