package com.ptithcm.forum.mapping;

import com.ptithcm.forum.dto.SystemCodeDto;
import com.ptithcm.forum.entity.SystemCode;

public interface SystemCodeMapping {

  SystemCode convertSystemCodeDtoToSystemCode(SystemCodeDto systemCodeDto);

  SystemCodeDto convertSystemCodeToSystemCodeDto(SystemCode systemCode);
}
