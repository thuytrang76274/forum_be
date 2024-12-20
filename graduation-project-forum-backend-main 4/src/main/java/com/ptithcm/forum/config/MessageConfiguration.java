package com.ptithcm.forum.config;

import com.ptithcm.forum.util.LanguageUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Component
public class MessageConfiguration extends AcceptHeaderLocaleResolver {

  @Override
  public Locale resolveLocale(HttpServletRequest request) {
    String language = request.getHeader("Accept-Language");
    if (StringUtils.isAllBlank(language) || language.equalsIgnoreCase("vi")) {
      return LanguageUtil.getVietnamese();
    }
    return LanguageUtil.getEnglish();
  }
}