package com.ptithcm.forum.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemCodeDto extends AuditDto {

  @Positive(message = "System code id must be a positive number")
  private Long id;
  @Size(min = 1, max = 50, message = "System code should have a length between 1 and 50 characters.")
  private String code;
  @JsonIgnore
  private LocalDateTime modifiedAt;
  @JsonIgnore
  private String modifiedBy;
}
