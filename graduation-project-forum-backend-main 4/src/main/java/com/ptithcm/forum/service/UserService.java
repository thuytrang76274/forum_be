package com.ptithcm.forum.service;

import com.ptithcm.forum.dto.LoginDto;
import com.ptithcm.forum.dto.PageDto;
import com.ptithcm.forum.dto.ResponseDto;
import com.ptithcm.forum.dto.TokenDto;
import com.ptithcm.forum.dto.UserDto;
import com.ptithcm.forum.dto.UserUpdateDto;

public interface UserService {

  ResponseDto<UserDto> createUser(UserDto user);

  ResponseDto<UserDto> updateUser(Long userId, UserUpdateDto user);

  ResponseDto<PageDto<UserDto>> getUsers(int page, int size, String sortBy, String direction);

  ResponseDto<UserDto> getSingleUser(Long userId);

  ResponseDto<TokenDto> logIn(LoginDto loginDto);

  ResponseDto<UserDto> getCurrentUser();
}
