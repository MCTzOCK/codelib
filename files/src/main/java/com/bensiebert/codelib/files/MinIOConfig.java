package com.bensiebert.codelib.files;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinIOConfig {

    private final MinIOProperties props;

    public MinIOConfig(MinIOProperties props) {
        this.props = props;
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(props.getUrl(), 443, true)
                .credentials(props.getAccessKey(), props.getSecretKey())
                .build();
    }
}
