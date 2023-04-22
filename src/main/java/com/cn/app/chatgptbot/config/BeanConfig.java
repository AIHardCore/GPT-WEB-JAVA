package com.cn.app.chatgptbot.config;

import com.cn.app.chatgptbot.model.wx.WxPay;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;


@Configuration
public class BeanConfig {

    @Value("${wx.pay.privateKey}")
    private String privateKey;

    @Resource
    WxPay wxPay;

    @Bean(name = "privateKey")
    public PrivateKey privateKey() throws IOException {
        // return PemUtil.loadPrivateKey(new ClassPathResource(wx.pay.privateKey).getInputStream());
        return PemUtil.loadPrivateKey(new FileInputStream(privateKey));
    }

    @Bean(name = "wechatPay2Credentials")
    public WechatPay2Credentials wechatPay2Credentials(@Qualifier("privateKey") PrivateKey privateKey) {
        return new WechatPay2Credentials(wxPay.getMchId(), new PrivateKeySigner(wxPay.getMchId(), privateKey));
    }

    @Bean(name = "autoUpdateCertificatesVerifier")
    public AutoUpdateCertificatesVerifier autoUpdateCertificatesVerifier(@Qualifier("wechatPay2Credentials") WechatPay2Credentials wechatPay2Credentials) throws UnsupportedEncodingException {
        return new AutoUpdateCertificatesVerifier(wechatPay2Credentials, wxPay.getSecret().getBytes("utf-8"));
    }

    @Bean(name = "wechatPay2Validator")
    public WechatPay2Validator wechatPay2Validator(@Qualifier("autoUpdateCertificatesVerifier") AutoUpdateCertificatesVerifier autoUpdateCertificatesVerifier) {
        return new WechatPay2Validator(autoUpdateCertificatesVerifier);
    }
}
