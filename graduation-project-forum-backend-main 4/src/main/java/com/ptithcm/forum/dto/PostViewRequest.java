package com.ptithcm.forum.dto;

import com.ptithcm.forum.entity.IssueStatus;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostViewRequest {
  private int page;
  private int size;
  private String typeDate;
  private LocalDate fromDate;
  private LocalDate toDate;
  private IssueStatus status;
  private String reporter;
  private List<Long> assigneeIds;
  private Long typeId;
  private Long customerId;
  private Long moduleId;
  private String version;
  private Boolean isDealCustomer;
}
