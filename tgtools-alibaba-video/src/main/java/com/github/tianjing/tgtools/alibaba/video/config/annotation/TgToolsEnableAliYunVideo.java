package com.github.tianjing.tgtools.alibaba.video.config.annotation;

import com.github.tianjing.tgtools.alibaba.video.config.AliyunVideoConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@ImportAutoConfiguration(value = {AliyunVideoConfig.class})
public @interface TgToolsEnableAliYunVideo {
}
