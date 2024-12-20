package com.ptithcm.forum.dto;

import com.ptithcm.forum.entity.ApplyFor;
import com.ptithcm.forum.entity.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto extends AuditDto {

  private Long id;
  @NotBlank(message = "${post.error.title.not.blank}")
  private String title;
  @NotBlank(message = "${post.error.description.not.blank}")
  private String description;
  private ApplyFor applyFor;
  private LocalDateTime approvedAt;
  private PostStatus status;
  private IssueDto issue;
  @NotNull(message = "${post.error.module.not.blank}")
  private SystemCodeDetailDto module;
  @NotNull(message = "${post.error.customer.not.blank}")
  private SystemCodeDetailDto customer;
  private List<CommentDto> comments;

  public boolean isWaitingApprove() {
    return PostStatus.WAITING_APPROVE.equals(this.status);
  }

  public boolean isAnalyst() {
    return PostStatus.ANALYST.equals(this.status);
  }

  public boolean isDone() {
    return PostStatus.DONE.equals(this.status);
  }

  public boolean isPending() {
    return PostStatus.PENDING.equals(this.status);
  }

  public boolean isClose() {
    return PostStatus.CLOSE.equals(this.status);
  }
}
