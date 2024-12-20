package com.ptithcm.forum.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ResponseDto<E> {

  private final E data;
  private final LocalDateTime timestamp;

  public ResponseDto(E data) {
    this.data = data;
    this.timestamp = LocalDateTime.now();
  }
}
