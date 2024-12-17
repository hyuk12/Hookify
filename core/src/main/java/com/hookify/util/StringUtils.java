package com.hookify.util;

public class StringUtils {

  public static String bytesToHex(byte[] bytes) {
    StringBuilder hexString = new StringBuilder();
    for (byte b : bytes) {
      hexString.append(String.format("%02x", b));
    }
    return hexString.toString();
  }
}
