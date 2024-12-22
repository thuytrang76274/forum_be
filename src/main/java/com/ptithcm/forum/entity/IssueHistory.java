package com.ptithcm.forum.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IssueHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(columnDefinition = "json")
  private String content;
  @ManyToOne
  @JoinColumn(name = "issue_id")
  private Issue issue;
  @Column(columnDefinition = "timestamp")
  private LocalDateTime modifiedAt;
  private String modifiedBy;
}
