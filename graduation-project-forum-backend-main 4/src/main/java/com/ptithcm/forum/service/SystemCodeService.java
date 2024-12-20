package com.ptithcm.forum.service;

import com.ptithcm.forum.dto.ResponseDto;
import com.ptithcm.forum.dto.SystemCodeDetailDto;
import com.ptithcm.forum.dto.SystemCodeDto;
import java.util.List;

public interface SystemCodeService {

  ResponseDto<List<SystemCodeDto>> getSystemCodes();

  ResponseDto<Object> getSystemCode(Long systemCodeId, Boolean isGetDetail);

  ResponseDto<SystemCodeDto> createSystemCode(SystemCodeDto systemCodeDto);

  ResponseDto<SystemCodeDetailDto> createSystemCodeDetail(Long systemCodeId,
      SystemCodeDetailDto systemCodeDetailDto);

  ResponseDto<SystemCodeDetailDto> updateSystemCodeDetail(Long systemCodeId,
      SystemCodeDetailDto systemCodeDetailDto);
}
