package com.cn.app.chatgptbot.service;

import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.wx.AccessTokenInfo;
import com.cn.app.chatgptbot.model.wx.PrepayResult;
import com.cn.app.chatgptbot.model.wx.WxUserInfo;

public interface IWxService {
    /**
     * 获取公众号信息
     * @return
     */
    B getGZHInfo();

    /**
     * 通过code换取网页授权access_token
     * @param code
     * @return
     */
    AccessTokenInfo getAccessTokenInfo(String code);

    /**
     * 拉取用户信息(需scope为 snsapi_userinfo)
     * @param accessToken
     * @param openId
     * @return
     */
    WxUserInfo getUserInfo(String accessToken, String openId);

    /**
     * 微信支付预下单
     *
     * @param openId    下单人openid
     * @param orderNo   订单编号
     * @param orderName 订单名称
     * @param total     订单金额，单位：分
     * @return
     */
    PrepayResult prepay(String openId, String orderNo, String orderName, long total);

    /**
     * 微信关闭订单
     *
     * @param tradeNo   订单编号
     * @return
     */
    B close(String tradeNo);

}
