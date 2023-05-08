package com.cn.app.chatgptbot.controller;

import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.ali.req.AliPayCreateReq;
import com.cn.app.chatgptbot.model.req.*;
import com.cn.app.chatgptbot.model.wx.PayCallBack;
import com.cn.app.chatgptbot.model.wx.PrepayResult;
import com.cn.app.chatgptbot.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 商品表(gptKey)表控制层
 *
 * @author  
 * @since 2022-03-12 15:23:19
 */
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


    @RequestMapping(value = "/return/url", method = RequestMethod.GET)
    @ApiOperation(value = "支付订单查询")
    public B returnUrl(@Validated @RequestBody ReturnUrlReq req) {
        return orderService.returnUrl(req);
    }

    @RequestMapping(value = "/callback")
    @ApiOperation(value = "支付回调")
    public PayCallBack callback(@RequestBody OrderCallBackReq req) {
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
