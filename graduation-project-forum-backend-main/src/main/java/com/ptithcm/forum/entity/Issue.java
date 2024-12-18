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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
public class Issue extends BaseCreateEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String content;
  private String penpotCommentId;
  private LocalDate dueDate;
  @Enumerated(EnumType.STRING)
  private IssueStatus status;
  private String version;
  private Boolean isAppendix = false;
  private Boolean isDealCustomer = false;
  @ManyToOne
  @JoinColumn(name = "image_id")
  private Image image;
  @ManyToOne
  @JoinColumn(name = "type_id")
  private SystemCodeDetail type;
  @ManyToOne
  @JoinColumn(name = "customer_id")
  private SystemCodeDetail customer;
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "issue")
  private List<IssueHistory> histories = new ArrayList<>();
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "issue")
  private List<Post> posts = new ArrayList<>();
  @ManyToMany(fetch = FetchType.LAZY, mappedBy = "issues")
  private Set<User> assignees = new HashSet<>();

  public boolean isNew() {
    return this.status.equals(IssueStatus.NEW);
  }

  public boolean isAssigned() {
    return this.status.equals(IssueStatus.ASSIGNED);
  }

  public boolean isDiscussing() {
    return this.status.equals(IssueStatus.DISCUSSING);
  }

  public boolean isAnalyst() {
    return this.status.equals(IssueStatus.ANALYST);
  }

  public boolean isDone() {
    return this.status.equals(IssueStatus.DONE);
  }

  public boolean isPending() {
    return this.status.equals(IssueStatus.PENDING);
  }

  public boolean isClose() {
    return this.status.equals(IssueStatus.CLOSE);
  }

  public void addHistory(IssueHistory history) {
    this.histories.add(history);
    history.setIssue(this);
  }

  public void addUser(User user) {
    this.assignees.add(user);
    user.getIssues().add(this);
  }

  public void addListUser(List<User> users) {
    for (User user : users) {
      addUser(user);
    }
  }

  public void removeUser(User user) {
    this.assignees.removeIf(u -> u.getId().equals(user.getId()));
    user.getIssues().removeIf(u -> u.getId().equals(this.getId()));
  }

  public void removeListUser(List<User> users) {
    for (User user : users) {
      removeUser(user);
    }
  }

  public void removeListUser(Set<User> users) {
    for (User user : users) {
      removeUser(user);
    }
  }

  public void addPost(Post post) {
    this.posts.add(post);
    post.setIssue(this);
  }

  public void addPosts(List<Post> posts) {
    for (Post post : posts) {
      addPost(post);
    }
  }

  public void removePost(Post post) {
    this.posts.removeIf(p -> p.getId().equals(post.getId()));
    post.setIssue(null);
  }

  public void removePosts(List<Post> posts) {
    for (Post post : posts) {
      removePost(post);
    }
  }
}
