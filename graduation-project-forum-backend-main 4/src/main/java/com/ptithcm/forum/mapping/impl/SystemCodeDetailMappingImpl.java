package com.ptithcm.forum.mapping.impl;

import com.ptithcm.forum.dto.SystemCodeDetailDto;
import com.ptithcm.forum.entity.SystemCodeDetail;
import com.ptithcm.forum.mapping.SystemCodeDetailMapping;
import org.springframework.stereotype.Component;

@Component
public class SystemCodeDetailMappingImpl implements SystemCodeDetailMapping {

  @Override
  public SystemCodeDetailDto convertSystemCodeDetailToSystemCodeDetailDto(
      SystemCodeDetail systemCodeDetail) {
    if (systemCodeDetail == null) {
      return null;
    }
    SystemCodeDetailDto systemCodeDetailDto = new SystemCodeDetailDto();
    systemCodeDetailDto.setId(systemCodeDetail.getId());
    systemCodeDetailDto.setCodeName(systemCodeDetail.getCodeName());
    systemCodeDetailDto.setDescription(systemCodeDetail.getDescription());
    systemCodeDetailDto.setSystemCodeId(systemCodeDetail.getSystemCode().getId());
    systemCodeDetailDto.setCreatedAt(systemCodeDetail.getCreatedAt());
    systemCodeDetailDto.setModifiedAt(systemCodeDetail.getModifiedAt());
    systemCodeDetailDto.setCreatedBy(systemCodeDetail.getCreatedBy());
    systemCodeDetailDto.setModifiedBy(systemCodeDetail.getModifiedBy());
    return systemCodeDetailDto;
  }

  @Override
  public SystemCodeDetail convertSystemCodeDetailDtoToSystemCodeDetail(
      SystemCodeDetailDto systemCodeDetailDto) {
    if (systemCodeDetailDto == null) {
      return null;
    }
    SystemCodeDetail systemCodeDetail = new SystemCodeDetail();
    systemCodeDetail.setCodeName(systemCodeDetailDto.getCodeName());
    systemCodeDetail.setDescription(systemCodeDetailDto.getDescription());
    return systemCodeDetail;
  }
}
