package com.ptithcm.forum.repository;

import com.ptithcm.forum.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  @Query("select u from User u where u.type <> 'ADMIN'")
  Page<User> getPageUserExcludeAdminUser(Pageable pageable);

  @Query("select u from User u where u.type <> 'ADMIN'")
  List<User> getListUserExcludeAdminUser();

  @Query("select u from User u where u in :userIds")
  List<User> getListUserByListUserId(List<Long> userIds);
}
