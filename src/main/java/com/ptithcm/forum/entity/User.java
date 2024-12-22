package com.ptithcm.forum.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String username;
  private String password;
  private String name;
  @Enumerated(EnumType.STRING)
  private UserType type;
  @Enumerated(EnumType.STRING)
  private UserStatus status;
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "assignee_issue",
      joinColumns = { @JoinColumn(name = "user_id") },
      inverseJoinColumns = { @JoinColumn(name = "issue_id") }
  )
  private Set<Issue> issues = new HashSet<>();

  public void addIssue(Issue issue) {
    this.issues.add(issue);
    issue.getAssignees().add(this);
  }

  public boolean isLocked() {
    return status == UserStatus.LOCKED;
  }

  public boolean isActive() {
    return status == UserStatus.ACTIVE;
  }

  public boolean isAdmin() {
    return type == UserType.ADMIN;
  }

  public boolean isUser() {
    return type == UserType.USER;
  }
}
