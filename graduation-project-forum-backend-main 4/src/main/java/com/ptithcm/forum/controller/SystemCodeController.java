package com.ptithcm.forum.controller;

import com.ptithcm.forum.dto.ResponseDto;
import com.ptithcm.forum.dto.SystemCodeDetailDto;
import com.ptithcm.forum.dto.SystemCodeDto;
import com.ptithcm.forum.service.SystemCodeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${rest.system-code}")
public class SystemCodeController {

  private final SystemCodeService systemCodeService;

  public SystemCodeController(SystemCodeService systemCodeService) {
    this.systemCodeService = systemCodeService;
  }

  @GetMapping
  public ResponseDto<List<SystemCodeDto>> getSystemCodes() {
    return systemCodeService.getSystemCodes();
  }

  @GetMapping("/{id}")
  public ResponseDto<Object> getSystemCodeOrGetDetails(@PathVariable("id") Long id,
      @RequestParam(value = "isGetDetail", defaultValue = "false") Boolean isGetDetail) {
    return systemCodeService.getSystemCode(id, isGetDetail);
  }

  @PostMapping
  @Validated
  public ResponseEntity<ResponseDto<SystemCodeDto>> createSystemCode(
      @RequestBody @Valid SystemCodeDto systemCodeDto) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(systemCodeService.createSystemCode(systemCodeDto));
  }

  @PostMapping("/{id}")
  @Validated
  public ResponseEntity<?> createSystemCodeDetail(@PathVariable("id") Long id,
      @RequestBody @Valid SystemCodeDetailDto systemCodeDetailDto) {
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(systemCodeService.createSystemCodeDetail(id, systemCodeDetailDto));
  }

  @PatchMapping("/{id}")
  @Validated
  public ResponseDto<SystemCodeDetailDto> updateSystemCodeDetail(
      @PathVariable("id")  Long id,
      @RequestBody @Valid SystemCodeDetailDto systemCodeDetailDto) {
    return systemCodeService.updateSystemCodeDetail(id, systemCodeDetailDto);
  }
}
