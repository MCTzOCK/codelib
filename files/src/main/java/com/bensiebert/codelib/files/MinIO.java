package com.bensiebert.codelib.files;

import io.minio.MinioClient;

public class MinIO {

    public static MinioClient getClient()  {
        return MinioClient.builder()
                .endpoint(System.getProperty("codelib.minio.url", "http://localhost:9000"))
                .credentials(
                        System.getProperty("codelib.minio.accessKey", "minioadmin"),
                        System.getProperty("codelib.minio.secretKey", "minioadmin")
                )
                .build();
    }
}
