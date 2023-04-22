package com.cn.app.chatgptbot.model.ali.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.sms")
public class SMSConfig implements Serializable {
    /**
     * AccessKey Id
     */
    private String accessKeyId;
    /**
     * AccessKey Secret
     */
    private String accessKeySecret;
    /**
     * api接口地址
     */
    private String api;
    /**
     * 短信模板CODE
     */
    private String templateCode;
    /**
     * 短信签名名称
     */
    private String signName;
    /**
     * 服务地址
     */
    private String regionID;
    /**
     * 短信模板变量对应的实际值
     */
    private String param = "{\"code\":\"%s\"}";
}
