package com.ptithcm.forum.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String title;
  private String description;
  @Enumerated(EnumType.STRING)
  private ApplyFor applyFor;
  private LocalDateTime approvedAt;
  @Enumerated(EnumType.STRING)
  private PostStatus status;
  @ManyToOne
  @JoinColumn(name = "issue_id")
  private Issue issue;
  @ManyToOne
  @JoinColumn(name = "module_id")
  private SystemCodeDetail module;
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
  private List<Comment> comments;

  public boolean isWaitingApprove() {
    return status.equals(PostStatus.WAITING_APPROVE);
  }

  public boolean isAnalyst() {
    return status.equals(PostStatus.ANALYST);
  }

  public boolean isDone() {
    return status.equals(PostStatus.DONE);
  }

  public boolean isPending() {
    return status.equals(PostStatus.PENDING);
  }

  public boolean isClose() {
    return status.equals(PostStatus.CLOSE);
  }
}
