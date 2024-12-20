package com.ptithcm.forum.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ptithcm.forum.entity.UserStatus;
import com.ptithcm.forum.entity.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto extends AuditDto {

  @NotBlank(message = "{user.error.username.not.blank}")
  private String username;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @NotBlank(message = "{user.error.password.not.blank}")
  private String password;
  private String name;
  @NotNull(message = "{user.error.type.not.blank}")
  private UserType type;
  private UserStatus status;
}
