package com.ptithcm.forum.mapping.impl;

import com.ptithcm.forum.dto.UserDto;
import com.ptithcm.forum.entity.User;
import com.ptithcm.forum.mapping.UserMapping;
import org.springframework.stereotype.Component;

@Component
public class UserMappingImpl implements UserMapping {

  @Override
  public User convertUserDtoToUser(UserDto userDto) {
    if (userDto == null) {
      return null;
    }
    User user = new User();
    user.setName(userDto.getName());
    user.setType(userDto.getType());
    user.setUsername(userDto.getUsername());
    return user;
  }

  @Override
  public UserDto convertUserToUserDto(User user) {
    if (user == null) {
      return null;
    }
    UserDto userDto = new UserDto();
    userDto.setId(user.getId());
    userDto.setName(user.getName());
    userDto.setType(user.getType());
    userDto.setStatus(user.getStatus());
    userDto.setUsername(user.getUsername());
    userDto.setPassword(user.getPassword());
    userDto.setCreatedAt(user.getCreatedAt());
    userDto.setModifiedAt(user.getModifiedAt());
    userDto.setCreatedBy(user.getCreatedBy());
    userDto.setModifiedBy(user.getModifiedBy());
    return userDto;
  }

  @Override
  public UserDto convertUserToUserDtoView(User user) {
    UserDto userDto = convertUserToUserDto(user);
    userDto.setPassword(null);
    return userDto;
  }
}
