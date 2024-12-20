package com.ptithcm.forum.util;

import com.ptithcm.forum.config.MinioConfiguration;
import com.ptithcm.forum.service.CommonService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import lombok.SneakyThrows;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MinioUtil {

  private final MinioClient minioClient;
  private final MinioConfiguration minioConfiguration;

  public MinioUtil(MinioClient minioClient, MinioConfiguration minioConfiguration,
      CommonService commonService) {
    this.minioClient = minioClient;
    this.minioConfiguration = minioConfiguration;
  }

  @SneakyThrows
  public void putObject(String bucketName, MultipartFile file, String fileName) {
    InputStream inputStream = new ByteArrayInputStream(file.getBytes());
    minioClient.putObject(
        PutObjectArgs.builder().bucket(bucketName).object(fileName)
            .stream(inputStream, -1, minioConfiguration.getImageSize())
            .build()
    );
  }

  @SneakyThrows
  public String putFileUrl(String bucketName, String fileUrl, String fileName) {
    URL url = new URL(fileUrl);
    URLConnection connection = url.openConnection();
    InputStream inputStream = connection.getInputStream();
    MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
    MimeType type = allTypes.forName(connection.getContentType());
    String fileExtension = type.getExtension();
    minioClient.putObject(
        PutObjectArgs.builder()
            .bucket(bucketName)
            .object(fileName + fileExtension)
            .stream(inputStream, connection.getContentLength(), -1)
            .build());
    return fileName + fileExtension;
  }

  @SneakyThrows
  public void removeObject(String bucketName, String fileName) {
    minioClient.removeObject(
        RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build()
    );
  }

  @SneakyThrows
  public InputStream getObject(String bucketName, String fileName) {
    return minioClient.getObject(
        GetObjectArgs.builder()
            .bucket(bucketName)
            .object(fileName).build()
    );
  }

  public String extractFileName(String url) {
    return url.substring(url.lastIndexOf("/") + 1);
  }
}
