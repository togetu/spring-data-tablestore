package com.github.tianjing.tgtools.alibaba.sms.client;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tianjing.tgtools.alibaba.sms.config.AliSmsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tgtools.exceptions.APPErrorException;

import java.util.regex.Pattern;

public class AliSmsClient {
    private static Logger log = LoggerFactory.getLogger(AliSmsClient.class);


    static String PHONE_REGEX = "^1\\d{10}$";
    private AliSmsConfig config;
    private com.aliyun.dysmsapi20170525.Client client;

    public void init(AliSmsConfig pAliSmsConfig) throws APPErrorException {
        config = pAliSmsConfig;
        try {
            Config vCconfig = new Config();
            // 您的AccessKey ID
            vCconfig.setAccessKeyId(config.getAccessKeyId());
            // 您的AccessKey Secret
            vCconfig.setAccessKeySecret(config.getAccessKeySecret());
            vCconfig.setEndpoint(config.getEndpoint());
            client = new com.aliyun.dysmsapi20170525.Client(vCconfig);
        } catch (Exception e) {
            throw new APPErrorException("创建短信服务客户端失败");
        }

    }

    public boolean phoneVerification(String phone) {
        return Pattern.compile(PHONE_REGEX).matcher(phone).find();
    }

    public void send(String phoneNumber, String tempId, String signName, ObjectNode param) throws APPErrorException {
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phoneNumber);
        request.setTemplateCode(tempId);
        request.setSignName(signName);
        request.setTemplateParam(param.toString());
        SendSmsResponse vResponse = null;
        try {
            vResponse = client.sendSms(request);
            validSendSmsResponse(vResponse);
        } catch (Exception e) {
            log.error("短息接口调用错误！;", e);
            throw new APPErrorException("短息接口调用错误！;" + e.getMessage());
        }

    }

    public void sendCode(String phoneNumber, String code) throws APPErrorException {
        if (!phoneVerification(phoneNumber)) {
            throw new APPErrorException("手机格式不对;" + phoneNumber);
        }
        final ObjectNode param = tgtools.util.JsonParseHelper.createObjectNode();
        param.put("code", code);
        send(phoneNumber, config.getTempId(), config.getSignName(), param);
    }

    protected void validSendSmsResponse(SendSmsResponse pSendSmsResponse) throws APPErrorException {
        if (!"OK".equals(pSendSmsResponse.getBody().getCode())) {
            throw new APPErrorException("短息接口返回失败；" + pSendSmsResponse.getBody().getMessage());
        }
    }



}
