package com.ptithcm.forum.repository;

import com.ptithcm.forum.entity.SystemCodeDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SystemCodeDetailRepository extends JpaRepository<SystemCodeDetail, Long> {

  @Query("select scd from SystemCodeDetail scd join fetch scd.systemCode sc where sc.id = :systemCodeId")
  List<SystemCodeDetail> getListSystemCodeDetailBySystemCodeId(Long systemCodeId);
  @Query("select scd from SystemCodeDetail scd join fetch scd.systemCode sc where sc.code = :code")
  List<SystemCodeDetail> getListSystemCodeDetailByCode(String code);
}
