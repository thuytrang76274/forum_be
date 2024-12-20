package com.ptithcm.forum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemCodeDetailDto extends AuditDto {

  private Long systemCodeId;
  @NotBlank(message = "Code name of system code must be not blank")
  private String codeName;
  private String description;
}
