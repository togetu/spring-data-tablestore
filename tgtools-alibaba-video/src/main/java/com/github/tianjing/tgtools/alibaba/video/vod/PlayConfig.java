package com.github.tianjing.tgtools.alibaba.video.vod;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

/**
 * https://help.aliyun.com/document_detail/86952.htm?spm=a2c4g.11186623.0.0.1312e3c8w0rLXG
 *
 * @author
 * @date 2022-01-24
 */
@Data
public class PlayConfig {
    /**
     * 播放域名名称。当配置多个播放域名时，可指定使用其中一个域名播放当前视频。当指定域名不存在时，播放地址返回当前视频存储对应的默认播放域名。取值示例："vod.test_domain"。
     */
    @JsonProperty("PlayDomain")
    String PlayDomain;

    /**
     * 客户端请求的真实IP。可用于验证审核安全IP等场景。可解决经过多层代理后，点播服务无法获取到原始客户端IP的问题。为了保障数据安全性，该IP值为加密后的值，加密方式为AES/ECB/PKCS5Padding，加密使用的密钥请提交工单后由点播后台处理。取值示例："yqCD7Fp1uqChoVj/sl/p5Q=="。
     */
    @JsonProperty("XForwardedFor")
    String XForwardedFor;

    /**
     * 视频点播试看时长，单位为秒。最小值1，最大值为视频总时长，未指定时表示观看完整视频，开启试看功能详见点播试看。
     */
    @JsonProperty("PreviewTime")
    Long PreviewTime;

    /**
     * 业务方令牌服务生成的MtsHlsUriToken，适用于HLS标准加密的视频播放，实现对业务方解密密钥的保护，防止密钥被窃取。
     */
    @JsonProperty("MtsHlsUriToken")
    String MtsHlsUriToken;


    /**
     * 加密类型，可用于筛选非加密或加密流进行播放。取值：
     * <p>
     * Unencrypted：非加密。
     * <p>
     * AliyunVoDEncryption：阿里云私有加密。
     * <p>
     * HLSEncryption：HLS标准加密。
     */
    @JsonProperty("EncryptType")
    String EncryptType;

    public void setEncryptType(EncryptType encryptType) {
        EncryptType = encryptType.getValue();
    }

    @Getter
    public enum EncryptType {
        UNENCRYPTED("非加密", "Unencrypted"),
        ALIYUN_VOD_ENCRYPTION("阿里云私有加密", "AliyunVoDEncryption"),
        HLS_ENCRYPTION("HLS标准加密", "HLSEncryption"),

        ;
        String text;
        String value;

        EncryptType(String text, String value) {
            this.text = text;
            this.value = value;
        }
    }

    @Override
    public String toString() {
        return tgtools.util.JsonParseHelper.parseToJson(this, false);
    }


}
