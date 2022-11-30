package com.github.tianjing.tgtools.alibaba.video.config;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.github.tianjing.tgtools.alibaba.video.template.OssFileTemplate;
import com.github.tianjing.tgtools.alibaba.video.template.impl.OssFileTemplateImpl;
import org.springframework.context.annotation.Bean;

/**
 * @author
 */
public class OssTemplateConfig {
    @Bean
    public AliyunOssTemplateConfig aliyunOssTemplateConfig() {
        return new AliyunOssTemplateConfig();
    }

    @Bean
    public OssFileTemplate ossFileTemplate(AliyunOssTemplateConfig pAliyunProductConfig) {
        OssFileTemplateImpl vOssFileTemplateImpl = new OssFileTemplateImpl();
        vOssFileTemplateImpl.setOssClient(createClient(pAliyunProductConfig));
        vOssFileTemplateImpl.setBucketName(pAliyunProductConfig.getBucketName());
        return vOssFileTemplateImpl;
    }

    private static OSS createClient(AliyunOssTemplateConfig pAliyunProductConfig) {
        // 创建ClientConfiguration实例，您可以根据实际情况修改默认参数。
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        // 设置是否支持CNAME。CNAME用于将自定义域名绑定到目标Bucket。
        conf.setSupportCname(true);
        conf.setVerifySSLEnable(false);

        // 创建OSSClient实例。
        return new OSSClientBuilder().build(pAliyunProductConfig.getEndpoint(), pAliyunProductConfig.getAccessKey(), pAliyunProductConfig.getAccessSecret(), conf);
    }
}
