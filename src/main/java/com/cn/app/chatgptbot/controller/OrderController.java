package com.cn.app.chatgptbot.controller;

import com.alibaba.fastjson.JSON;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.ali.req.AliPayCreateReq;
import com.cn.app.chatgptbot.model.req.*;
import com.cn.app.chatgptbot.model.wx.PayCallBack;
import com.cn.app.chatgptbot.model.wx.PrepayResult;
import com.cn.app.chatgptbot.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 商品表(gptKey)表控制层
 *
 * @author  
 * @since 2022-03-12 15:23:19
 */
@Log4j2
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Api(tags = {"订单管理"})
public class OrderController {


    /**
     * gptKeyService
     */
    final IOrderService orderService;


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ApiOperation(value = "创建预订单")
    public B<PrepayResult> createOrder(@Validated @RequestBody CreateOrderReq req) {
        return orderService.createOrder(req);
    }

    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    @ApiOperation(value = "支付订单")
    public B<PrepayResult> payOrder(@Validated @RequestBody PayOrderReq req) {
        return orderService.payOrder(req);
    }

    @RequestMapping(value = "/return/url", method = RequestMethod.POST)
    @ApiOperation(value = "支付订单查询")
    public B returnUrl(@Validated @RequestBody ReturnUrlReq req) {
        return orderService.returnUrl(req);
    }

    /*@RequestMapping(value = "/callback")
    @ApiOperation(value = "支付回调")
    public PayCallBack callback(HttpServletRequest request, HttpServletResponse response) {
        log.info("微信回调开始：{}", JSON.toJSONString(request.getParameterMap()));
        return orderService.callback(null);
    }*/

    @RequestMapping(value = "/callback")
    @ApiOperation(value = "支付回调")
    public PayCallBack callback(@RequestBody OrderCallBackReq req) {
        log.info("微信回调开始：{}", JSON.toJSONString(req));
        return orderService.callback(req);
    }

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ApiOperation(value = "查询订单列表")
    public B query(@Validated @RequestBody QueryOrderReq req) {
        return orderService.query(req);
    }

    @RequestMapping(value = "/ali/create", method = RequestMethod.POST)
    @ApiOperation(value = "支付宝创建预订单")
    public synchronized  B<String> aliCreateOrder(@Validated @RequestBody AliPayCreateReq req) throws Exception {
        return orderService.aliCreateOrder(req);
    }

    @RequestMapping(value = "/ali/callBack")
    @ApiOperation(value = "支付宝支付回调")
    public synchronized String aliCallBack(HttpServletRequest request) throws Exception {
        return orderService.aliCallBack(request);
    }
}
