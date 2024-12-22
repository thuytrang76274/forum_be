package com.ptithcm.forum.mapping;

import com.ptithcm.forum.dto.UserDto;
import com.ptithcm.forum.entity.User;

public interface UserMapping {

  User convertUserDtoToUser(UserDto userCreateDto);

  UserDto convertUserToUserDto(User user);

  UserDto convertUserToUserDtoView(User user);
}
