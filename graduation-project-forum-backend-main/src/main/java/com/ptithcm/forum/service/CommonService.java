package com.ptithcm.forum.service;

import com.ptithcm.forum.entity.User;
import java.util.Set;

public interface CommonService {

  User getCurrentUserLoggedIn();

  boolean isCurrentUserLoggedInOneOfUsers(Set<User> users, User currentUser);

  String getMessage(String key);
}
