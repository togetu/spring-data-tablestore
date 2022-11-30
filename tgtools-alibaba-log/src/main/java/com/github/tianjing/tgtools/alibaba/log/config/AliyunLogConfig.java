package com.github.tianjing.tgtools.alibaba.log.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author
 */
@Data
@ConfigurationProperties(prefix = "tgtools.alibaba.Log")
public class AliyunLogConfig {

    private String endpoint;
    private String accessKey;
    private String accessSecret;
    private String projectName;
    private String logStore;
}
