package com.cn.app.chatgptbot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.Order;
import com.cn.app.chatgptbot.model.ali.req.AliPayCreateReq;
import com.cn.app.chatgptbot.model.req.*;
import com.cn.app.chatgptbot.model.wx.PayCallBack;
import com.cn.app.chatgptbot.model.wx.PrepayResult;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户表(User)表服务接口
 *
 * @author  
 * @since 2022-03-12 15:23:17
 */
public interface IOrderService extends IService<Order> {


    B<PrepayResult> createOrder(CreateOrderReq req);

    B<PrepayResult> payOrder(PayOrderReq req);

    B returnUrl(ReturnUrlReq req);

    PayCallBack callback(OrderCallBackReq req);

    B query(QueryOrderReq req);

    B<String> aliCreateOrder(AliPayCreateReq req) throws Exception;

    String  aliCallBack(HttpServletRequest request) throws Exception;





}
