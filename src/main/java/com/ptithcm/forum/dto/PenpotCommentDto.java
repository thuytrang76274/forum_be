package com.ptithcm.forum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PenpotCommentDto {

  private String id;
  private String content;
  private String frameId;
}
