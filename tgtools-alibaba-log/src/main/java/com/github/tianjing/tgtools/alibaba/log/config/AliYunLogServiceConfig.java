package com.github.tianjing.tgtools.alibaba.log.config;

import com.github.tianjing.tgtools.alibaba.log.client.AliYunLogProducerClient;
import org.springframework.context.annotation.Bean;

public class AliYunLogServiceConfig {

    @Bean
    public AliyunLogConfig aliyunLogConfig() {
        return new AliyunLogConfig();
    }

    @Bean
    public AliYunLogProducerClient aliYunLogClient(AliyunLogConfig pAliyunLogConfig) {
        AliYunLogProducerClient vClient = new AliYunLogProducerClient();
        vClient.init(pAliyunLogConfig.getAccessSecret(), pAliyunLogConfig.getAccessKey(), pAliyunLogConfig.getEndpoint()
                , pAliyunLogConfig.getProjectName(), pAliyunLogConfig.getLogStore());
        return vClient;
    }

}
