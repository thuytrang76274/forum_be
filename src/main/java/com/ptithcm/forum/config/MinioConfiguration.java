package com.ptithcm.forum.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Slf4j
public class MinioConfiguration {

  @Value("${minio.url}")
  private String endpoint;
  @Value("${minio.access.key}")
  private String accessKey;
  @Value("${minio.access.secret}")
  private String secretKey;
  @Value("${minio.bucket}")
  private String bucketName;
  @Value("${minio.image-size}")
  private Long imageSize;

  @Bean
  @SneakyThrows
  public MinioClient minioClient() {
    log.info("Start creating minio client");
    MinioClient minioClient = MinioClient.builder().credentials(accessKey, secretKey)
        .endpoint(endpoint).build();
    try {
      boolean found = minioClient.bucketExists(
          BucketExistsArgs.builder().bucket(bucketName).build());
      if (!found) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
      }
    } catch (Exception e) {
      log.error(e.getMessage());
      throw e;
    }
    return minioClient;
  }
}
