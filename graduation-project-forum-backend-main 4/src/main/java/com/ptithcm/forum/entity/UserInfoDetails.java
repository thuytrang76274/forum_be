package com.ptithcm.forum.entity;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserInfoDetails implements UserDetails {

  private final String username;
  private final String password;
  @Getter
  private final String name;
  private final Collection<? extends GrantedAuthority> authorities;
  private final boolean isEnabled;

  public UserInfoDetails(User user) {
    this.username = user.getUsername();
    this.password = user.getPassword();
    this.authorities = Stream.of(user.getType())
        .map(t -> new SimpleGrantedAuthority(t.toString())).collect(Collectors.toList());
    this.isEnabled = user.getStatus().equals(UserStatus.ACTIVE);
    this.name = user.getName();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return isEnabled;
  }

}
