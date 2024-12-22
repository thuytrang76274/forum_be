package com.ptithcm.forum.util;

import java.util.Locale;

public class LanguageUtil {

  private LanguageUtil() {
  }

  public static Locale getVietnamese() {
    return new Locale("vi", "V");
  }

  public static Locale getEnglish() {
    return new Locale("en");
  }
}
