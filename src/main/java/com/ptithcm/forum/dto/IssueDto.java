package com.ptithcm.forum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.ptithcm.forum.entity.IssueStatus;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueDto extends AuditCreateDto {

  private String content;
  private String penpotCommentId;
  private String penpotPrototypeLink;
  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate dueDate;
  private IssueStatus status;
  private String version;
  private Boolean isAppendix;
  private Boolean isDealCustomer;
  private ImageDto image;
  private SystemCodeDetailDto type;
  private SystemCodeDetailDto customer;
  private List<UserDto> assignees;
  private Integer numberOfPosts;

  public boolean isAssigneeEmpty() {
    return assignees == null || assignees.isEmpty();
  }

  public boolean isNew() {
    return IssueStatus.NEW.equals(this.status);
  }

  public boolean isAssigned() {
    return IssueStatus.ASSIGNED.equals(this.status);
  }

  public boolean isDiscussing() {
    return IssueStatus.DISCUSSING.equals(this.status);
  }

  public boolean isAnalyst() {
    return IssueStatus.ANALYST.equals(this.status);
  }

  public boolean isDone() {
    return IssueStatus.DONE.equals(this.status);
  }

  public boolean isPending() {
    return IssueStatus.PENDING.equals(this.status);
  }

  public boolean isClose() {
    return IssueStatus.CLOSE.equals(this.status);
  }
}
