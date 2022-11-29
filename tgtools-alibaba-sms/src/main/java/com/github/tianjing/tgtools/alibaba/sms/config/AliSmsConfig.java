package com.github.tianjing.tgtools.alibaba.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tgtools.alibaba.sms")
public class AliSmsConfig {
    private String accessKeyId;
    private String accessKeySecret;
    private String tempId;
    private String signName;
    private String endpoint = "dysmsapi.aliyuncs.com";
}
