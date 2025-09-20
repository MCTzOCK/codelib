package com.bensiebert.codelib.files;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Duration;

@Service
public class MinIOStorageService {

    private final MinioClient client;
    private final String bucket;

    public MinIOStorageService(MinioClient client, MinIOProperties props) {
        this.client = client;
        this.bucket = props.getBucket();
    }

    public String uploadFile(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            String objectName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return objectName;
        } catch (MinioException e) {
            throw new RuntimeException("MinIO error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("File upload error: " + e.getMessage(), e);
        }
    }

    public String getPresignedUrl(String objectName, Duration duration) {
        try {
            return client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectName)
                            .expiry((int) duration.getSeconds())
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error generating presigned URL: " + e.getMessage(), e);
        }
    }
}
