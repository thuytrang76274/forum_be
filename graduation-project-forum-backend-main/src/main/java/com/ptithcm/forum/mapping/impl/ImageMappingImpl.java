package com.ptithcm.forum.mapping.impl;

import com.ptithcm.forum.dto.ImageDto;
import com.ptithcm.forum.entity.Image;
import com.ptithcm.forum.mapping.ImageMapping;
import org.springframework.stereotype.Component;

@Component
public class ImageMappingImpl implements ImageMapping {

  @Override
  public ImageDto convertImageToImageDto(Image image) {
    if (image == null) {
      return null;
    }
    ImageDto imageDto = new ImageDto();
    imageDto.setId(image.getId());
    imageDto.setImageUrl(image.getImageUrl());
    imageDto.setCreatedAt(image.getCreatedAt());
    imageDto.setCreatedBy(image.getCreatedBy());
    return imageDto;
  }
}
