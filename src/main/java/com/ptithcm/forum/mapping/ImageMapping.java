package com.ptithcm.forum.mapping;

import com.ptithcm.forum.dto.ImageDto;
import com.ptithcm.forum.entity.Image;

public interface ImageMapping {
  ImageDto convertImageToImageDto(Image image);
}
