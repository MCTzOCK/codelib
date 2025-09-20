package com.bensiebert.codelib.files;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "codelib.minio")
public class MinIOProperties {
    private String url, accessKey, secretKey, bucket;
}
