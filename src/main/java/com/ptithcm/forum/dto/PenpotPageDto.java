package com.ptithcm.forum.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PenpotPageDto {
  private String id;
  private Map<String, PenpotPageFlowDto> flows;
}
