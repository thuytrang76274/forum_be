package com.ptithcm.forum.config;

import com.ptithcm.forum.entity.User;
import com.ptithcm.forum.entity.UserInfoDetails;
import com.ptithcm.forum.exception.NotFoundException;
import com.ptithcm.forum.repository.UserRepository;
import com.ptithcm.forum.util.LanguageUtil;
import java.util.List;
import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class ApplicationConfiguration {

  private final UserRepository userRepository;

  public ApplicationConfiguration(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return username -> {
      User user = userRepository.findByUsername(username)
          .orElseThrow(() -> new NotFoundException(
              String.format("User not found with username %s", username)));
      return new UserInfoDetails(user);
    };
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setPasswordEncoder(passwordEncoder());
    authProvider.setUserDetailsService(userDetailsService());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
      throws Exception {
    return configuration.getAuthenticationManager();
  }

  @Bean
  public ReloadableResourceBundleMessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("classpath:i18n/messages");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

  @Bean
  public LocaleResolver localeResolver() {
    AcceptHeaderLocaleResolver localeResolver = new MessageConfiguration();
    localeResolver.setSupportedLocales(
        List.of(LanguageUtil.getEnglish(), LanguageUtil.getVietnamese()));
    localeResolver.setDefaultLocale(LanguageUtil.getEnglish());
    return localeResolver;
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public Tika tika() {
    return new Tika();
  }
}
