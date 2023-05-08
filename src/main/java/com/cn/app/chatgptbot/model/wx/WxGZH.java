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
@ConfigurationProperties(prefix = "wx.gzh")
/**
 * 公众号配置
 */
public class WxGZH {
    /**
     * 公众号AppID
     */
    private String appId;
    /**
     * 公众号appSecret
     */
    @JSONField(serialize=false)
    private String appSecret;
    /**
     * 获取用户code的回调地址
     */
    private String redirectUri;
    /**
     * 获取用户code的url
     */
    private String codeUrl;
    /**
     * 获取access_token的url
     */
    private String accessTokenUrl;
    /**
     * 刷新access_token的url
     */
    private String refreshAccessTokenUrl;
    /**
     * 获取用户信息的url
     */
    private String userinfoUrl;
}
