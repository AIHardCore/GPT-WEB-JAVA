package com.cn.app.chatgptbot.model.wx;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
@ConfigurationProperties(prefix = "wx.pay")
/**
 *
 */
public class WxPay {
    /**
     * 直连商户号
     */
    private String mchId;
    /**
     * 证书序列号
     */
    private String serialNumber;
    /**
     * appid
     */
    private String appId;
    /**
     * APIv3密钥
     */
    @JSONField(serialize=false)
    private String secret;
    /**
     * 支付Url
     */
    private String payUrl;
    /**
     * 支付通知的回调地址
     */
    private String notifyUrl;
    /**
     * 订单关闭地址
     */
    private String closeUrl;
    /**
     * 订单超时时间(分钟)
     */
    private int timeOut = 10;
}
