package com.ptithcm.forum.service.impl;

import com.ptithcm.forum.dto.ResponseDto;
import com.ptithcm.forum.dto.SystemCodeDetailDto;
import com.ptithcm.forum.dto.SystemCodeDto;
import com.ptithcm.forum.entity.SystemCode;
import com.ptithcm.forum.entity.SystemCodeDetail;
import com.ptithcm.forum.entity.User;
import com.ptithcm.forum.exception.NotFoundException;
import com.ptithcm.forum.mapping.SystemCodeDetailMapping;
import com.ptithcm.forum.mapping.SystemCodeMapping;
import com.ptithcm.forum.repository.SystemCodeDetailRepository;
import com.ptithcm.forum.repository.SystemCodeRepository;
import com.ptithcm.forum.service.CommonService;
import com.ptithcm.forum.service.SystemCodeService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class SystemCodeServiceImpl implements SystemCodeService {

  private static final String SYSTEM_CODE_NOT_FOUND = "System Code with id %s not found";
  private static final String SYSTEM_CODE_DETAIL_NOT_FOUND = "System Code detail with id %s not found";
  private final SystemCodeRepository systemCodeRepository;
  private final SystemCodeDetailRepository systemCodeDetailRepository;
  private final SystemCodeMapping systemCodeMapping;
  private final SystemCodeDetailMapping systemCodeDetailMapping;
  private final CommonService commonService;

  public SystemCodeServiceImpl(SystemCodeRepository systemCodeRepository,
      SystemCodeDetailRepository systemCodeDetailRepository, SystemCodeMapping systemCodeMapping,
      SystemCodeDetailMapping systemCodeDetailMapping, CommonService commonService) {
    this.systemCodeRepository = systemCodeRepository;
    this.systemCodeDetailRepository = systemCodeDetailRepository;
    this.systemCodeMapping = systemCodeMapping;
    this.systemCodeDetailMapping = systemCodeDetailMapping;
    this.commonService = commonService;
  }

  @Override
  public ResponseDto<List<SystemCodeDto>> getSystemCodes() {
    return new ResponseDto<>(systemCodeRepository.findAll()
        .stream()
        .map(systemCodeMapping::convertSystemCodeToSystemCodeDto)
        .collect(Collectors.toList())
    );
  }

  @Override
  public ResponseDto<Object> getSystemCode(Long systemCodeId, Boolean isGetDetail) {
    if (isGetDetail) {
      return new ResponseDto<>(
          systemCodeDetailRepository.getListSystemCodeDetailBySystemCodeId(systemCodeId).stream()
              .map(systemCodeDetailMapping::convertSystemCodeDetailToSystemCodeDetailDto)
              .collect(Collectors.toList()));
    }
    return new ResponseDto<>(
        systemCodeMapping.convertSystemCodeToSystemCodeDto(getSystemCodeById(systemCodeId)));
  }

  @Override
  @Transactional
  public ResponseDto<SystemCodeDto> createSystemCode(SystemCodeDto systemCodeDto) {
    SystemCode systemCode = systemCodeMapping.convertSystemCodeDtoToSystemCode(systemCodeDto);
    User user = commonService.getCurrentUserLoggedIn();
    LocalDateTime now = LocalDateTime.now();
    systemCode.setCreatedAt(now);
    systemCode.setCreatedBy(user.getUsername());
    SystemCode systemCodeSaved = systemCodeRepository.save(systemCode);
    return new ResponseDto<>(systemCodeMapping.convertSystemCodeToSystemCodeDto(systemCodeSaved));
  }

  @Override
  @Transactional
  public ResponseDto<SystemCodeDetailDto> createSystemCodeDetail(Long systemCodeId,
      SystemCodeDetailDto systemCodeDetailDto) {
    User user = commonService.getCurrentUserLoggedIn();
    SystemCode systemCode = getSystemCodeById(systemCodeId);
    SystemCodeDetail systemCodeDetail = systemCodeDetailMapping
        .convertSystemCodeDetailDtoToSystemCodeDetail(systemCodeDetailDto);
    LocalDateTime now = LocalDateTime.now();
    systemCodeDetail.setCreatedAt(now);
    systemCodeDetail.setModifiedAt(now);
    systemCodeDetail.setCreatedBy(user.getUsername());
    systemCodeDetail.setModifiedBy(user.getUsername());
    systemCodeDetail.setSystemCode(systemCode);
    SystemCodeDetail systemCodeDetailSaved = systemCodeDetailRepository.save(systemCodeDetail);
    return new ResponseDto<>(systemCodeDetailMapping
        .convertSystemCodeDetailToSystemCodeDetailDto(systemCodeDetailSaved)
    );
  }

  @Override
  @Transactional
  public ResponseDto<SystemCodeDetailDto> updateSystemCodeDetail(Long systemCodeId,
      SystemCodeDetailDto systemCodeDetailDto) {
    getSystemCodeById(systemCodeId);
    User user = commonService.getCurrentUserLoggedIn();
    LocalDateTime now = LocalDateTime.now();
    SystemCodeDetail systemCodeDetail = getSystemCodeDetailById(systemCodeDetailDto.getId());
    systemCodeDetail.setCodeName(systemCodeDetailDto.getCodeName());
    systemCodeDetail.setDescription(systemCodeDetailDto.getDescription());
    systemCodeDetail.setModifiedAt(now);
    systemCodeDetail.setModifiedBy(user.getUsername());
    SystemCodeDetail systemCodeDetailSaved = systemCodeDetailRepository.save(systemCodeDetail);
    return new ResponseDto<>(
        systemCodeDetailMapping.convertSystemCodeDetailToSystemCodeDetailDto(systemCodeDetailSaved)
    );
  }

  private SystemCode getSystemCodeById(Long id) {
    return systemCodeRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(String.format(SYSTEM_CODE_NOT_FOUND, id)));
  }

  private SystemCodeDetail getSystemCodeDetailById(Long id) {
    return systemCodeDetailRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(String.format(SYSTEM_CODE_DETAIL_NOT_FOUND, id)));
  }
}
