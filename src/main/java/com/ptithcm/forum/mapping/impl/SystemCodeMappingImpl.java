package com.ptithcm.forum.mapping.impl;

import com.ptithcm.forum.dto.SystemCodeDto;
import com.ptithcm.forum.entity.SystemCode;
import com.ptithcm.forum.mapping.SystemCodeMapping;
import org.springframework.stereotype.Component;

@Component
public class SystemCodeMappingImpl implements SystemCodeMapping {

  @Override
  public SystemCode convertSystemCodeDtoToSystemCode(SystemCodeDto systemCodeDto) {
    if (systemCodeDto == null) {
      return null;
    }
    SystemCode systemCode = new SystemCode();
    systemCode.setCode(systemCodeDto.getCode());
    return systemCode;
  }

  @Override
  public SystemCodeDto convertSystemCodeToSystemCodeDto(SystemCode systemCode) {
    if (systemCode == null) {
      return null;
    }
    SystemCodeDto systemCodeDto = new SystemCodeDto();
    systemCodeDto.setId(systemCode.getId());
    systemCodeDto.setCode(systemCode.getCode());
    systemCodeDto.setCreatedAt(systemCode.getCreatedAt());
    systemCodeDto.setCreatedBy(systemCode.getCreatedBy());
    return systemCodeDto;
  }
}
