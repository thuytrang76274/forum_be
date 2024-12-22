package com.ptithcm.forum.exception;

public class UserLockedException extends RuntimeException {

  public UserLockedException(String message) {
    super(message);
  }
}
