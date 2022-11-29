package com.github.tianjing.tgtools.alibaba.video.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jimmy jiang
 */
@Data
@ConfigurationProperties(prefix = "tgtools.alibaba.oss")
public class AliyunOssTemplateConfig {

    private String endpoint;
    private String accessKey;
    private String accessSecret;
    private String bucketName;


}
