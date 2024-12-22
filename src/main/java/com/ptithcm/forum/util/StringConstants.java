package com.ptithcm.forum.util;

import java.util.List;

public class StringConstants {

  private StringConstants() {
  }

  public static final String USER_NOT_FOUND = "user.error.not.found";
  public static final String USER_EXISTS = "user.error.exists";
  public static final String USER_INVALID = "user.error.invalid";
  public static final String USER_LOCKED = "user.error.locked";
  public static final String USER_NOT_ALLOWED = "user.error.not.allowed";
  public static final String ISSUE_NOT_FOUND = "issue.error.not.found";
  public static final String MODULE_NOT_FOUND = "Module not found";
  public static final String POST_NOT_FOUND = "post.error.not.found";
  public static final String PENPOT_REQUEST_ERROR = "penpot.error.request";
  public static final String COMMENT_NOT_FOUND = "comment.error.not.found";
  public static final String DUE_DATE_NOT_ACCEPTABLE = "issue.error.due.date.not.acceptable";
  public static final String MINIO_ISSUE = "minio.error.issue";
  public static final String TYPE_NOT_FOUND = "issue.error.type.not.found";
  public static final String FROM_DATE_TO_DATE_ERROR = "issue.error.from.date.to.date.error";
  public static final List<String> SORT_STRING_ACCEPT = List.of("asc", "desc");
  public static final List<String> ISSUE_DATE_TYPE_ACCEPT = List.of("dueDate", "createdAt");
  public static final List<String> POST_DATE_TYPE_ACCEPT = List.of("dueDate", "createdAt",
      "approveAt");
  public static final String SYSTEM = "system";
}
