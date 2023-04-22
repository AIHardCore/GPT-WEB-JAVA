package com.cn.app.chatgptbot.model.wx;

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
     * appid
     */
    private String appId;
    /**
     * APIv3密钥
     */
    private String secret;
    /**
     * 支付Url
     */
    private String payUrl;
    /**
     * 支付通知的回调地址
     */
    private String notifyUrl;
}
