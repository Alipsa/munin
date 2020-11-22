package se.alipsa.munin.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Generates random passwords */
public final class PasswordGenerator {

  private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
  private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
  private static final String NUMBER = "0123456789";
  private static final String OTHER_CHAR = "!@#$*_-.()[]";

  private PasswordGenerator() {
    // utility class
  }

  public static String generateRandomPassword(int length) {
    if (length < 1) throw new IllegalArgumentException();

    SecureRandom random = new SecureRandom();
    String passwordChars = shuffleString(CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR);

    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int rndCharAt = random.nextInt(passwordChars.length());
      char rndChar = passwordChars.charAt(rndCharAt);
      sb.append(rndChar);
    }
    return sb.toString();
  }

  private static String shuffleString(String string) {
    List<String> letters = Arrays.asList(string.split(""));
    Collections.shuffle(letters);
    return String.join("", letters);
  }

  public static String encrypt(String passwd) {
    return new BCryptPasswordEncoder().encode(String.valueOf(passwd));
  }

}