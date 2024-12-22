package com.ptithcm.forum.service.impl;

import com.ptithcm.forum.entity.User;
import com.ptithcm.forum.entity.UserInfoDetails;
import com.ptithcm.forum.exception.NotFoundException;
import com.ptithcm.forum.exception.UserLockedException;
import com.ptithcm.forum.repository.UserRepository;
import com.ptithcm.forum.service.CommonService;
import com.ptithcm.forum.util.StringConstants;
import java.util.Set;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CommonServiceImpl implements CommonService {

  private final UserRepository userRepository;
  private final ReloadableResourceBundleMessageSource messageSource;

  public CommonServiceImpl(UserRepository userRepository,
      ReloadableResourceBundleMessageSource messageSource) {
    this.userRepository = userRepository;
    this.messageSource = messageSource;
  }

  @Override
  public User getCurrentUserLoggedIn() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserInfoDetails userInfoDetails = (UserInfoDetails) authentication.getPrincipal();
    User user = userRepository.findByUsername(userInfoDetails.getUsername())
        .orElseThrow(() -> new NotFoundException(getMessage(StringConstants.USER_INVALID)));
    validateUserIsLocked(user);
    return user;
  }

  @Override
  public boolean isCurrentUserLoggedInOneOfUsers(Set<User> users, User currentUser) {
    for (User user : users) {
      if (user.getId().equals(currentUser.getId())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String getMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }

  private void validateUserIsLocked(User user) {
    if (user.isLocked()) {
      throw new UserLockedException(StringConstants.USER_LOCKED);
    }
  }
}
