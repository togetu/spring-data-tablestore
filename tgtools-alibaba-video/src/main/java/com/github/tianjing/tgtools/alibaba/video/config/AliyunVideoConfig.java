package com.github.tianjing.tgtools.alibaba.video.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author
 */
@Data
@ConfigurationProperties(prefix = "tgtools.alibaba.video")
public class AliyunVideoConfig {

    private String endpoint;
    private String accessKey;
    private String accessSecret;
    private String bucketName;


}
