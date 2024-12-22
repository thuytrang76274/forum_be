package com.ptithcm.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto extends AuditDto {

  private Long id;
  @NotBlank(message = "${comment.error.content.not.blank}")
  private String content;
  private Long vote;
  private Boolean isSolution;
  @NotNull(message = "${comment.error.post.not.blank}")
  private PostDto post;
}
