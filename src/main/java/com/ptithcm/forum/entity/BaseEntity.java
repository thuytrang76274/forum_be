package com.ptithcm.forum.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class BaseEntity {

  @Column(columnDefinition = "timestamp")
  private LocalDateTime createdAt;
  private String createdBy;
  @Column(columnDefinition = "timestamp")
  private LocalDateTime modifiedAt;
  private String modifiedBy;
}
