package com.github.tianjing.tgtools.alibaba.oss.config.annotation;

import com.github.tianjing.tgtools.alibaba.oss.config.AliyunOssTemplateConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@ImportAutoConfiguration(value = {AliyunOssTemplateConfig.class})
public @interface TgToolsEnableOssTemplate {
}
