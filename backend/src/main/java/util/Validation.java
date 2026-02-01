package com.payflow.util;

public class Validation {

  public static void require(boolean condition, String message) {
    if (!condition) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void notNull(Object o, String message) {
    if (o == null) {
      throw new IllegalArgumentException(message);
    }
  }
}
