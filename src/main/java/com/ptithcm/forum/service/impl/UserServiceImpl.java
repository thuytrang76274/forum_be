package com.ptithcm.forum.service.impl;

import com.ptithcm.forum.dto.LoginDto;
import com.ptithcm.forum.dto.PageDto;
import com.ptithcm.forum.dto.ResponseDto;
import com.ptithcm.forum.dto.TokenDto;
import com.ptithcm.forum.dto.UserDto;
import com.ptithcm.forum.dto.UserUpdateDto;
import com.ptithcm.forum.entity.User;
import com.ptithcm.forum.entity.UserStatus;
import com.ptithcm.forum.exception.DataExistException;
import com.ptithcm.forum.exception.NotAllowedException;
import com.ptithcm.forum.exception.NotFoundException;
import com.ptithcm.forum.mapping.UserMapping;
import com.ptithcm.forum.repository.UserRepository;
import com.ptithcm.forum.service.CommonService;
import com.ptithcm.forum.service.UserService;
import com.ptithcm.forum.util.JwtUtil;
import com.ptithcm.forum.util.StringConstants;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

  private final AuthenticationManager authenticationManager;
  private final UserMapping userMapping;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final CommonService commonService;
  private final UserRepository userRepository;

  public UserServiceImpl(AuthenticationManager authenticationManager, UserMapping userMapping,
      PasswordEncoder passwordEncoder, JwtUtil jwtUtil, CommonService commonService,
      UserRepository userRepository) {
    this.authenticationManager = authenticationManager;
    this.userMapping = userMapping;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.commonService = commonService;
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public ResponseDto<UserDto> createUser(UserDto user) {
    User currentUser = commonService.getCurrentUserLoggedIn();
    if (currentUser.isUser()) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    validateDuplicateUser(user);
    User newUser = userMapping.convertUserDtoToUser(user);
    newUser.setPassword(passwordEncoder.encode(user.getPassword()));
    newUser.setStatus(UserStatus.ACTIVE);
    setAuditInformation(currentUser, newUser, true);
    User savedUser = userRepository.save(newUser);
    return new ResponseDto<>(userMapping.convertUserToUserDtoView(savedUser));
  }

  @Override
  public ResponseDto<List<UserDto>> getAllUsers() {
    return new ResponseDto<>(userRepository.findAll().stream().map(
        userMapping::convertUserToUserDtoView).collect(Collectors.toList()));
  }

  @Override
  @Transactional
  public ResponseDto<UserDto> updateUser(Long userId, UserUpdateDto userUpdate) {
    User currentUserLoggedIn = commonService.getCurrentUserLoggedIn();
    User currentUser = userRepository.findById(userId)
        .orElseThrow(
            () -> new NotFoundException(commonService.getMessage(StringConstants.USER_NOT_FOUND)));

    if (!isAllowedToModify(currentUserLoggedIn, currentUser)) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }

    switch (currentUserLoggedIn.getType()) {
      case ADMIN:
        updateForAdminUser(currentUser, currentUserLoggedIn, userUpdate);
        break;
      case USER:
        updateForNormalUser(currentUser, userUpdate);
        break;
    }

    setAuditInformation(currentUserLoggedIn, currentUser, false);
    User savedUser = userRepository.save(currentUser);
    return new ResponseDto<>(userMapping.convertUserToUserDtoView(savedUser));
  }

  @Override
  public ResponseDto<PageDto<UserDto>> getUsers(int page, int size, String sortBy,
      String direction) {
    User currentUserLoggedIn = commonService.getCurrentUserLoggedIn();
    if (currentUserLoggedIn.isUser()) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    int realPage = Math.max(page, 0);
    int realSize = size <= 0 ? 10 : size;
    if (!StringConstants.SORT_STRING_ACCEPT.contains(direction.toLowerCase())) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    Sort sort = null;
    if (direction.equalsIgnoreCase("asc")) {
      sort = Sort.by(sortBy).ascending();
    }
    if (direction.equalsIgnoreCase("desc")) {
      sort = Sort.by(sortBy).descending();
    }
    Pageable pageable = PageRequest.of(realPage, realSize, sort);
    Page<User> users = userRepository.getPageUserExcludeAdminUser(pageable);
    return new ResponseDto<>(
        PageDto.<UserDto>builder()
            .page(realPage + 1)
            .size(realSize)
            .totalElements(users.getTotalPages())
            .hasNext(users.hasNext())
            .hasPrevious(users.hasPrevious())
            .data(
                users.stream().map(userMapping::convertUserToUserDtoView)
                    .collect(Collectors.toList()))
            .build());
  }

  @Override
  public ResponseDto<UserDto> getSingleUser(Long userId) {
    return new ResponseDto<>(userMapping.convertUserToUserDtoView(
        userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(
                commonService.getMessage(StringConstants.USER_NOT_FOUND)))));
  }

  @Override
  public ResponseDto<TokenDto> logIn(LoginDto loginDto) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtil.generateToken(loginDto.getUsername());
    return new ResponseDto<>(new TokenDto(jwt));
  }

  @Override
  public ResponseDto<UserDto> getCurrentUser() {
    return new ResponseDto<>(
        userMapping.convertUserToUserDtoView(commonService.getCurrentUserLoggedIn()));
  }

  private void updateForAdminUser(User currentUser, User currentLoggedInUser,
      UserUpdateDto userUpdate) {
    if (userUpdate.isUpdateStatus()
        && isCurrentUserWantToChangeItsSelf(currentLoggedInUser, currentUser)) {
      throw new NotAllowedException(commonService.getMessage(StringConstants.USER_NOT_ALLOWED));
    }
    updateStatusForUser(currentUser, userUpdate);
    updateNameForUser(currentUser, userUpdate);
    updatePasswordForUser(currentUser, userUpdate);
  }

  private void updateForNormalUser(User currentUser, UserUpdateDto userUpdate) {
    updateStatusForUser(currentUser, userUpdate);
    updateNameForUser(currentUser, userUpdate);
    updatePasswordForUser(currentUser, userUpdate);
  }

  private void updateStatusForUser(User currentUser, UserUpdateDto userUpdate) {
    currentUser.setStatus(
        userUpdate.isUpdateStatus() ? userUpdate.getStatus() : currentUser.getStatus());
  }

  private void updateNameForUser(User currentUser, UserUpdateDto userUpdate) {
    currentUser.setName(userUpdate.isUpdateName() ? userUpdate.getName() : currentUser.getName());
  }

  private void updatePasswordForUser(User currentUser, UserUpdateDto userUpdate) {
    currentUser.setPassword(
        userUpdate.isUpdatePassword() ? passwordEncoder.encode(userUpdate.getPassword())
            : currentUser.getPassword());
  }

  private void validateDuplicateUser(UserDto user) {
    String username = user.getUsername();
    userRepository.findByUsername(username).ifPresent(u -> {
      throw new DataExistException(commonService.getMessage(StringConstants.USER_EXISTS));
    });
  }

  private void setAuditInformation(User currentLoggedInUser, User newUser, boolean isCreate) {
    LocalDateTime now = LocalDateTime.now();
    if (isCreate) {
      newUser.setCreatedAt(now);
      newUser.setCreatedBy(currentLoggedInUser.getUsername());
    }
    newUser.setModifiedAt(now);
    newUser.setModifiedBy(currentLoggedInUser.getUsername());
  }

  private boolean isCurrentUserWantToChangeItsSelf(User currentLoggedInUser, User currentUser) {
    return currentLoggedInUser.getId().equals(currentUser.getId());
  }

  private boolean isAllowedToModify(User currentLoggedInUser, User currentUser) {
    if (currentLoggedInUser.isAdmin()) {
      return true;
    }
    return currentLoggedInUser.isUser() && isCurrentUserWantToChangeItsSelf(currentLoggedInUser,
        currentUser);
  }
}
