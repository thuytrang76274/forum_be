package com.ptithcm.forum.mapping;

import com.ptithcm.forum.dto.SystemCodeDetailDto;
import com.ptithcm.forum.entity.SystemCodeDetail;

public interface SystemCodeDetailMapping {

  SystemCodeDetailDto convertSystemCodeDetailToSystemCodeDetailDto(
      SystemCodeDetail systemCodeDetail);

  SystemCodeDetail convertSystemCodeDetailDtoToSystemCodeDetail(
      SystemCodeDetailDto systemCodeDetailDto);
}
