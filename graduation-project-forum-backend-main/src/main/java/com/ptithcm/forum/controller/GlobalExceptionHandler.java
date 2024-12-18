package com.ptithcm.forum.controller;

import com.ptithcm.forum.dto.MessageDto;
import com.ptithcm.forum.dto.ResponseDto;
import com.ptithcm.forum.dto.ValidationDto;
import com.ptithcm.forum.exception.DataExistException;
import com.ptithcm.forum.exception.DateTimeException;
import com.ptithcm.forum.exception.NotAllowedException;
import com.ptithcm.forum.exception.NotFoundException;
import com.ptithcm.forum.exception.PenpotException;
import com.ptithcm.forum.exception.UserLockedException;
import io.minio.errors.MinioException;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ResponseDto<MessageDto>> handleNotFoundException(
      NotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(new ResponseDto<>(MessageDto.builder()
            .code(HttpStatus.NOT_FOUND)
            .message(ex.getMessage())
            .build())
        );
  }

  @ExceptionHandler(DataExistException.class)
  public ResponseEntity<ResponseDto<MessageDto>> handleDataExistException(
      DataExistException ex) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(new ResponseDto<>(MessageDto.builder()
            .code(HttpStatus.CONFLICT)
            .message(ex.getMessage())
            .build())
        );
  }

  @ExceptionHandler(UserLockedException.class)
  public ResponseEntity<ResponseDto<MessageDto>> handleUserLockedException(UserLockedException ex) {
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(new ResponseDto<>(MessageDto.builder()
            .code(HttpStatus.FORBIDDEN)
            .message(ex.getMessage())
            .build())
        );
  }

  @ExceptionHandler(MinioException.class)
  public ResponseEntity<ResponseDto<MessageDto>> handleMinioException(MinioException ex) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ResponseDto<>(MessageDto.builder()
            .code(HttpStatus.INTERNAL_SERVER_ERROR)
            .message(ex.getMessage())
            .build()));
  }

  @ExceptionHandler(NotAllowedException.class)
  public ResponseEntity<ResponseDto<MessageDto>> handleNotAllowedException(NotAllowedException ex) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(new ResponseDto<>(MessageDto.builder()
            .code(HttpStatus.CONFLICT)
            .message(ex.getMessage())
            .build()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ResponseDto<MessageDto>> handleIllegalArgumentException(
      IllegalArgumentException ex) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(new ResponseDto<>(MessageDto.builder()
            .code(HttpStatus.CONFLICT)
            .message(ex.getMessage())
            .build()));
  }

  @ExceptionHandler(DateTimeException.class)
  public ResponseEntity<ResponseDto<MessageDto>> handleDateTimeException(DateTimeException ex) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(new ResponseDto<>(MessageDto.builder()
            .code(HttpStatus.CONFLICT)
            .message(ex.getMessage())
            .build()));
  }

  @ExceptionHandler(PenpotException.class)
  public ResponseEntity<ResponseDto<MessageDto>> handlePenpotException(PenpotException ex) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ResponseDto<>(MessageDto.builder()
            .code(HttpStatus.INTERNAL_SERVER_ERROR)
            .message(ex.getMessage())
            .build()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ResponseDto<List<ValidationDto>>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    List<ValidationDto> errors = new ArrayList<>();
    ex.getBindingResult().getFieldErrors().forEach(error -> {
      errors.add(
          ValidationDto.builder().field(error.getField()).message(error.getDefaultMessage())
              .build());
    });
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto<>(errors));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ResponseDto<List<String>>> handleConstraintViolationException(
      ConstraintViolationException ex) {
    List<String> errors = new ArrayList<>();
    ex.getConstraintViolations().forEach(error -> {
      errors.add(ex.getMessage().split(": ")[1]);
    });
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDto<>(errors));
  }
}
