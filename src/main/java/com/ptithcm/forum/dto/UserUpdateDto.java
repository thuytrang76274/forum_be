package com.ptithcm.forum.dto;

import com.ptithcm.forum.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

  private String password;
  private String name;
  private UserStatus status;

  public boolean isUpdatePassword() {
    return StringUtils.isNotBlank(password);
  }

  public boolean isUpdateName() {
    return StringUtils.isNotBlank(name);
  }

  public boolean isUpdateStatus() {
    return null != status;
  }
}
