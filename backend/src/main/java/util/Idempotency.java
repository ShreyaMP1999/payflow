package com.payflow.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory idempotency helper (portfolio-safe).
 * Prevents duplicate processing of the same key.
 */
public class Idempotency {

  private static final Set<String> KEYS = ConcurrentHashMap.newKeySet();

  public static boolean isDuplicate(String key) {
    return !KEYS.add(key);
  }

  public static void clear(String key) {
    KEYS.remove(key);
  }
}
