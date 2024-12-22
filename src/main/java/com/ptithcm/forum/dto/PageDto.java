package com.ptithcm.forum.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDto<E> {

  private int page;
  private int size;
  private int totalPages;
  private long totalElements;
  private List<E> data = new ArrayList<>();
  private boolean hasPrevious;
  private boolean hasNext;
}
