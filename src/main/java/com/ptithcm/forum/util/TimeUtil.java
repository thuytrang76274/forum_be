package com.ptithcm.forum.util;

import java.time.LocalDate;

public class TimeUtil {

  private TimeUtil() {
  }

  public static boolean isEqualOrBeforeDateNow(LocalDate date) {
    LocalDate now = LocalDate.now();
    return date.isBefore(now) || date.isEqual(now);
  }

  public static boolean isBeforeDateNow(LocalDate date) {
    LocalDate now = LocalDate.now();
    return date.isBefore(now);
  }
}
