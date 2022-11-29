package com.github.tianjing.tgtools.alibaba.video.vod;

import lombok.Data;

@Data
public class AliVideoConfig {
    private String accessKey;
    private String accessSecret;
    private String regionId;
    private String endpoint;
}
