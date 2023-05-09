package com.cn.app.chatgptbot.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.base.ResultEnum;
import com.cn.app.chatgptbot.constant.RedisKey;
import com.cn.app.chatgptbot.exception.CustomException;
import com.cn.app.chatgptbot.model.wx.*;
import com.cn.app.chatgptbot.service.IWxService;
import com.cn.app.chatgptbot.utils.RedisUtil;
import com.cn.app.chatgptbot.utils.WXPayUtil;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信Service
 */
@Service
@Slf4j
public class WxServiceImpl implements IWxService {
    @Resource
    private WechatPay2Credentials wechatPay2Credentials;
    @Resource
    private WechatPay2Validator wechatPay2Validator;
    @Resource
    private PrivateKey privateKey;

    /**
     * 获取公众号信息
     *
     * @return
     */
    @Override
    public B getGZHInfo() {
        WxGZH wxGZH = RedisUtil.getCacheObject(RedisKey.WX_GZH.getName());
        return B.okBuild(wxGZH);
    }

    /**
     * 根据code获取openId
     *
     * @param code
     * @return
     */
    @Override
    public AccessTokenInfo getAccessTokenInfo(String code) {
        WxGZH wxGZH = RedisUtil.getCacheObject(RedisKey.WX_GZH.getName());
        String url = wxGZH.getAccessTokenUrl().replace("{CODE}",code);
        String result = HttpUtil.get(url);
        AccessTokenInfo accessTokenInfo = JSON.parseObject(result,AccessTokenInfo.class);
        if (accessTokenInfo.getErrcode() != null){
            throw new CustomException("获取用户授权信息失败！");
        }
        return accessTokenInfo;
    }

    /**
     * 拉取用户信息(需scope为 snsapi_userinfo)
     *
     * @param accessToken
     * @param openId
     * @return
     */
    @Override
    public WxUserInfo getUserInfo(String accessToken, String openId) {
        WxGZH wxGZH = RedisUtil.getCacheObject(RedisKey.WX_GZH.getName());
        String url = wxGZH.getUserinfoUrl().replace("{ACCESS_TOKEN}",accessToken).replace("{OPENID}",openId);
        String result = HttpUtil.get(url);
        WxUserInfo userInfo = JSON.parseObject(result,WxUserInfo.class);
        if (userInfo.getErrcode() != null){
            log.error("获取用户授权信息失败:{}",JSON.toJSONString(result));
            throw new CustomException("获取用户信息失败！");
        }
        return userInfo;
    }

    /**
     * 微信支付预下单
     *
     * @param openId    下单人openid
     * @param orderNo   订单编号
     * @param orderName 订单名称
     * @param total     订单金额，单位：分
     * @return
     */
    public PrepayResult prepay(String openId, String orderNo, String orderName, long total) {
        WxPay wxPay = RedisUtil.getCacheObject(RedisKey.WX_PAY.getName());
        WxGZH wxGZH = RedisUtil.getCacheObject(RedisKey.WX_GZH.getName());
        Map params = new HashMap<>();
        params.put("appid", wxGZH.getAppId());
        params.put("mchid", wxPay.getMchId());
        params.put("description", orderName);
        params.put("out_trade_no", orderNo);
        params.put("notify_url", wxPay.getNotifyUrl());
        Map amount = new HashMap();
        amount.put("total", total);
        params.put("amount", amount);
        Map payer = new HashMap();
        payer.put("openid", openId);
        params.put("payer", payer);
        String body = JSONObject.toJSONString(params);

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            // 初始化httpClient
            httpClient = WechatPayHttpClientBuilder.create().
                    withCredentials(wechatPay2Credentials).
                    withValidator(wechatPay2Validator).build();
            HttpPost httpPost = new HttpPost(wxPay.getPayUrl());
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setEntity(new StringEntity(body, "utf-8"));
            long currentTimeMillis = System.currentTimeMillis();
            log.info(currentTimeMillis + "请求统一下单接口：" + body);
            // 由客户端执行(发送)POST请求
            response = httpClient.execute(httpPost);
            // 从响应模型中获取响应实体
            String jsonStr = EntityUtils.toString(response.getEntity());
            log.info(currentTimeMillis + "统一下单响应：" + jsonStr);
            JSONObject json = JSONObject.parseObject(jsonStr);
            PrepayResult result = new PrepayResult();
            if (json.containsKey("code")){
                log.error("下单失败:{}",json.getString("code"));
                result.setCode(json.getString("code"));
                result.setMsg("下单失败");
            }
            result.setAppId(wxGZH.getAppId());
            result.setTimeStamp(String.valueOf(WXPayUtil.getCurrentTimestamp()));
            result.setNonceStr(WXPayUtil.generateNonceStr());
            result.setSignType("RSA");
            result.setPackage("prepay_id=" + json.getString("prepay_id"));
            // 用私钥对信息进行数字签名
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update((wxGZH.getAppId() + "\n" +
                    result.getTimeStamp() + "\n" +
                    result.getNonceStr() + "\n" +
                    result.getPackage() + "\n").getBytes("utf-8"));
            result.setPaySign(Base64.getEncoder().encodeToString(signature.sign()));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 微信关闭订单
     *
     * @param tradeNo 订单编号
     * @return
     */
    @Override
    public B close(String tradeNo) {
        WxPay wxPay = RedisUtil.getCacheObject(RedisKey.WX_PAY.getName());
        Map params = new HashMap<>();
        params.put("mchid", wxPay.getMchId());
        String body = JSONObject.toJSONString(params);
        int statusCode = 1;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            // 初始化httpClient
            httpClient = WechatPayHttpClientBuilder.create().
                    withCredentials(wechatPay2Credentials).
                    withValidator(wechatPay2Validator).build();
            HttpPost httpPost = new HttpPost(wxPay.getCloseUrl().replace("{out_trade_no}",tradeNo));
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");
            httpPost.setEntity(new StringEntity(body, "utf-8"));
            long currentTimeMillis = System.currentTimeMillis();
            log.info(currentTimeMillis + ",【{}】订单关闭接口：" + body,tradeNo);
            // 由客户端执行(发送)POST请求
            response = httpClient.execute(httpPost);
            statusCode = response.getStatusLine().getStatusCode();
            log.info("订单关闭结果状态码：{}",statusCode);
            if (statusCode == 204){
                return B.okBuild();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return B.finalBuild(String.format("订单【%s】关闭失败！",tradeNo));
    }
}
