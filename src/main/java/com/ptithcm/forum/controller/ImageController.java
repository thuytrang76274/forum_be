package com.ptithcm.forum.controller;

import com.ptithcm.forum.util.MinioUtil;
import java.io.InputStream;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/image")
@Controller
public class ImageController {
  private final MinioUtil minioUtil;

  public ImageController(MinioUtil minioUtil) {
    this.minioUtil = minioUtil;
  }

  @SneakyThrows
  @GetMapping(value = "/{bucketName}/{name}", produces = {MediaType.IMAGE_GIF_VALUE,
      MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
  public @ResponseBody byte[] getImage(@PathVariable String bucketName, @PathVariable String name) {
    InputStream inputStream = minioUtil.getObject(bucketName, name);
    return IOUtils.toByteArray(inputStream);
  }
}
