package com.ptithcm.forum.controller;

import com.ptithcm.forum.dto.LoginDto;
import com.ptithcm.forum.dto.PageDto;
import com.ptithcm.forum.dto.ResponseDto;
import com.ptithcm.forum.dto.TokenDto;
import com.ptithcm.forum.dto.UserDto;
import com.ptithcm.forum.dto.UserUpdateDto;
import com.ptithcm.forum.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rest.user}")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseDto<PageDto<UserDto>> getUsers(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "10") int size,
      @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
      @RequestParam(name = "direction", defaultValue = "desc") String direction) {
    return userService.getUsers(page, size, sortBy, direction);
  }

  @GetMapping("/all-users")
  public ResponseDto<List<UserDto>> getUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/current-user")
  public ResponseDto<UserDto> getCurrentUser() {
    return userService.getCurrentUser();
  }

  @GetMapping("/{id}")
  public ResponseDto<UserDto> getUser(@PathVariable("id") Long userId) {
    return userService.getSingleUser(userId);
  }

  @PostMapping
  @Validated
  public ResponseEntity<ResponseDto<UserDto>> createUser(@RequestBody @Valid UserDto user) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
  }

  @PatchMapping("/{id}")
  @Validated
  public ResponseEntity<ResponseDto<UserDto>> updateUser(@PathVariable("id") Long userId,
      @RequestBody UserUpdateDto user) {
    return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userId, user));
  }

  @PostMapping("/login")
  public ResponseEntity<ResponseDto<TokenDto>> login(@RequestBody LoginDto user) {
    return ResponseEntity.ok(userService.logIn(user));
  }
}
