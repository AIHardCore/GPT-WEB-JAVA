package com.cn.app.chatgptbot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.base.PayWay;
import com.cn.app.chatgptbot.base.RedisDelayQueueEnum;
import com.cn.app.chatgptbot.config.ali.AliPayConfig;
import com.cn.app.chatgptbot.constant.RedisKey;
import com.cn.app.chatgptbot.dao.OrderDao;
import com.cn.app.chatgptbot.exception.CustomException;
import com.cn.app.chatgptbot.model.*;
import com.cn.app.chatgptbot.model.ali.AlipayNotifyParam;
import com.cn.app.chatgptbot.model.ali.req.AliPayCreateReq;
import com.cn.app.chatgptbot.model.req.*;
import com.cn.app.chatgptbot.model.res.CreateOrderRes;
import com.cn.app.chatgptbot.model.res.QueryOrderRes;
import com.cn.app.chatgptbot.model.res.ReturnUrlRes;
import com.cn.app.chatgptbot.model.wx.PayCallBack;
import com.cn.app.chatgptbot.model.wx.PrepayResult;
import com.cn.app.chatgptbot.model.wx.WxPay;
import com.cn.app.chatgptbot.service.*;
import com.cn.app.chatgptbot.utils.JwtUtil;
import com.cn.app.chatgptbot.utils.RedisDelayQueueUtil;
import com.cn.app.chatgptbot.utils.RedisUtil;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service("orderService")
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class OrderServiceImpl extends ServiceImpl<OrderDao, Order> implements IOrderService {

    @Resource
    private IProductService productService;

    @Resource
    private IUserService userService;

    @Resource
    private IRefuelingKitService refuelingKitService;

    @Resource
    private IWxService wxService;

    @Resource
    private RedisDelayQueueUtil redisDelayQueueUtil;

    @Override
    public synchronized  B<PrepayResult> createOrder(CreateOrderReq req) {
        Product product = productService.getById(req.getProductId());
        if (null == product) {
            return B.finalBuild("商品异常");
        }
        if(product.getStock() < req.getPayNumber()){
            return B.finalBuild("库存不足");
        }
        User user = userService.getById(JwtUtil.getUserId());
        //状态检查
        if (user.getDeleted()){
            log.error(String.format("用户：id[%s]、name[%s]状态异常！下单失败！",user.getId(),user.getName()));
            throw new CustomException(String.format("用户：id[%s]、name[%s]状态异常！下单失败！",user.getId(),user.getName()));
        }
        Order order = new Order();
        order.setPayNumber(req.getPayNumber());
        order.setProductId(req.getProductId());
        order.setPayType(req.getType());
        order.setUserId(user.getId());
        order.setPrice(product.getPrice().multiply(new BigDecimal(req.getPayNumber())));
        order.setPayType(req.getType());
        order.setCreateTime(LocalDateTime.now());
        String orderNo = IdUtil.getSnowflake(1,1).nextIdStr();
        order.setTradeNo(orderNo);
        this.save(order);

        //将价格转成微信的价格单位：分
        long total = product.getPrice().multiply(new BigDecimal(100)).longValue();
        PrepayResult result = wxService.prepay(user.getOpenId(),orderNo,product.getName(),total);
        log.info("PrepayResult：{}", JSON.toJSONString(result));
        Map<String, String> map = new HashMap<>();
        map.put("tradeNo", order.getTradeNo());
        map.put("remark", "订单支付超时，自动取消订单");
        WxPay wxPay = RedisUtil.getCacheObject(RedisKey.WX_PAY.getName());
        redisDelayQueueUtil.addDelayQueue(map, wxPay.getTimeOut(), TimeUnit.MINUTES, RedisDelayQueueEnum.ORDER_PAYMENT_TIMEOUT.getCode());
        return B.okBuild(result);
    }

    @Override
    public B<PrepayResult> payOrder(PayOrderReq req) {
        Order order = this.getById(req.getOrderId());
        if (order.getState() == 3){
            return B.finalBuild("订单状态异常无法支付，请联系客服处理！");
        }
        if (order.getState() == 2){
            return B.finalBuild("订单已超时，请重新下单！");
        }
        if (order.getState() == 1){
            return B.finalBuild("订单已支付");
        }

        User user = userService.getById(JwtUtil.getUserId());
        //状态检查
        if (user.getDeleted()){
            log.error(String.format("用户：id[%s]、name[%s]状态异常！下单失败！",user.getId(),user.getName()));
            throw new CustomException(String.format("用户：id[%s]、name[%s]状态异常！下单失败！",user.getId(),user.getName()));
        }
        if (!order.getUserId().equals(user.getId())){
            log.error(String.format("原订单用户：id[%s]、现用户：id[%s]！不能支付别人的订单！",order.getUserId(),user.getId()));
            throw new CustomException("不能支付别人的订单");
        }
        Product product = productService.getById(order.getProductId());
        if (null == product) {
            return B.finalBuild("商品异常");
        }
        if(product.getStock() < order.getPayNumber()){
            return B.finalBuild("库存不足");
        }
        //将价格转成微信的价格单位：分
        long total = product.getPrice().multiply(new BigDecimal(100)).longValue();
        PrepayResult result = wxService.prepay(user.getOpenId(),order.getTradeNo(),product.getName(),total);
        log.info("PrepayResult：{}", JSON.toJSONString(result));
        return B.okBuild(result);
    }

    @Override
    public B returnUrl(ReturnUrlReq req) {
        Order order = this.getById(req.getOrderId());
        if(order.getState() == 1){
            return B.finalBuild("订单已完成");
        }
        PayConfig payConfig = RedisUtil.getCacheObject("payConfig");
//        PayConfig payConfig = payConfigService.lambdaQuery().list().get(0);
        Map<String, Object> map = new HashMap<>();
        map.put("act","order");
        map.put("pid",payConfig.getPid());
        map.put("key",payConfig.getSecretKey());
        map.put("out_trade_no",req.getOrderId());
        String orderPayInfo = HttpUtil.get("https://www.11zhifu.cn/api.php", map);
        ReturnUrlRes returnUrlRes = JSONObject.parseObject(orderPayInfo, ReturnUrlRes.class);
        if (returnUrlRes.getCode() != 1) {
            return B.finalBuild("订单支付异常");
        }
        order.setPayType(returnUrlRes.getType());
        order.setTradeNo(returnUrlRes.getTrade_no());
        BigDecimal money = returnUrlRes.getMoney();
        if (money.compareTo(order.getPrice()) != 0) {
            log.info("支付失败,支付金额异常，支付金额：{},订单金额：{}", money, order.getPrice());
            order.setMsg("支付失败,支付金额异常，支付金额：" + money + "订单金额" + order.getPrice());
            order.setState(2);
        }
        if (returnUrlRes.getStatus() == 1) {
            order.setState(1);
            order.setMsg("支付成功");
        } else {
            log.info("支付失败,支付状态：{}", returnUrlRes.getStatus());
            order.setMsg("支付失败,支付状态：" + returnUrlRes.getStatus());
            order.setState(2);
        }
        if(null != returnUrlRes.getEndtime()){
            LocalDateTime endTime = LocalDateTime.parse(returnUrlRes.getEndtime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            order.setOperateTime(endTime);
        }
       if(null != returnUrlRes.getAddtime()){
           LocalDateTime addTime = LocalDateTime.parse(returnUrlRes.getAddtime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
           order.setCreateTime(addTime);
       }
        this.saveOrUpdate(order);
        if (order.getState() == 1) {
            Product product = productService.getById(order.getProductId());
            User user = userService.getById(order.getUserId());
            if (product.getType() == 0) {
                //次数
                user.setRemainingTimes(user.getRemainingTimes() + product.getNumberTimes());
            }
            if (product.getType() == 1) {
                //月卡
                if (LocalDateTime.now().compareTo(user.getExpirationTime()) < 0) {
                    user.setExpirationTime(user.getExpirationTime().plusDays(30L));
                } else {
                    user.setExpirationTime(LocalDateTime.now().plusDays(30L));
                }
                user.setCardDayMaxNumber(product.getMonthlyNumber());
                user.setType(1);
            }
            if (product.getType() == 2) {
                //加油包
                RefuelingKit kit = new RefuelingKit();
                kit.setProductId(product.getId());
                kit.setNumberTimes(product.getNumberTimes());
                kit.setUserId(order.getUserId());
                refuelingKitService.save(kit);
            }
            userService.updateById(user);
            if(order.getState() == 1){
                product.setStock(product.getStock() - order.getPayNumber());
                productService.saveOrUpdate(product);
            }
        }
        return B.okBuild();
    }

    @Override
    public PayCallBack callback(OrderCallBackReq req) {
        WxPay wxPay = RedisUtil.getCacheObject(RedisKey.WX_PAY.getName());
        AesUtil aesUtil = new AesUtil(wxPay.getSecret().getBytes());
        try {
            //验签
            String s = aesUtil.decryptToString(req.getResource().getAssociatedData().getBytes(), req.getResource().getNonce().getBytes(), req.getResource().getCiphertext());
            OrderCallBackReq.ResourceDTO resourceDTO = JSON.parseObject(s,OrderCallBackReq.ResourceDTO.class);
            req.setResourceDTO(resourceDTO);
            JSONObject jsonObject = JSONObject.parseObject(s);
            //订单号
            String orderId = jsonObject.getString("out_trade_no");
            //微信支付订单号
            String transactionId = jsonObject.getString("transaction_id");
            log.info("支付开始回调，回调参数：{}",req.toString());
            Order order = query(resourceDTO.getOutTradeNo());
            PayCallBack callBack = new PayCallBack("FAIL","业务处理异常");
            if (order == null){
                log.info("回调处理失败，订单{}不存在！", order.getTradeNo());
                return callBack;
            }
            if(order.getState() == 1){
                return new PayCallBack("SUCCESS","处理完成");
            }
            order.setPayType(PayWay.WX_PAY); //暂时只有微信支付
            order.setTradeNo(order.getTradeNo());
            BigDecimal money = new BigDecimal(resourceDTO.getAmount().getTotal());
            BigDecimal amount = order.getPrice().multiply(new BigDecimal(100));
            if (money.compareTo(amount) != 0) {
                log.info("支付失败,支付金额异常，支付金额(分)：{},订单金额(分)：{}", money, order.getPrice());
                order.setMsg(String.format("支付失败,支付金额异常，支付金额(分)：%s,订单金额(分)：%s",money,order.getPrice()));
                order.setState(3);
                return callBack;
            }
            if (resourceDTO.getTradeState().equals("SUCCESS")) {
                log.info("订单编号：{},支付成功",order.getTradeNo());
                order.setState(1);
                order.setMsg("支付成功");
            } else {
                log.info("支付失败,支付状态：{}", resourceDTO.getTradeState());
                order.setMsg("支付失败,支付状态：" + resourceDTO.getTradeState());
                order.setState(3);
            }
            order.setOperateTime(LocalDateTime.now());
            this.saveOrUpdate(order);
            if (order.getState() == 1) {
                Product product = productService.getById(order.getProductId());
                if (product.getType() == 0) {
                    //次数
                    userService.accumulationTimes(product.getNumberTimes(),order.getUserId());
                }else {
                    User user = userService.getById(order.getUserId());
                    if (product.getType() == 1) {
                        //月卡
                        if(null == user.getExpirationTime()){
                            user.setExpirationTime(LocalDateTime.now().plusDays(30L * order.getPayNumber()));
                        } else if (LocalDateTime.now().compareTo(user.getExpirationTime()) < 0) {
                            user.setExpirationTime(user.getExpirationTime().plusDays(30L * order.getPayNumber()));
                        } else {
                            user.setExpirationTime(LocalDateTime.now().plusDays(30L * 3 * order.getPayNumber()));
                        }
                        user.setCardDayMaxNumber(product.getMonthlyNumber());
                        user.setType(2);
                    }
                    if (product.getType() == 2) {
                        //季卡
                        if(null == user.getExpirationTime()){   //未开通
                            user.setExpirationTime(LocalDateTime.now().plusDays(30L * 3 * order.getPayNumber()));
                        } else if (LocalDateTime.now().compareTo(user.getExpirationTime()) < 0) {   //未过期
                            user.setExpirationTime(user.getExpirationTime().plusDays(30L * 3 * order.getPayNumber()));
                        } else {    //已过期
                            user.setExpirationTime(LocalDateTime.now().plusDays(30L * 3 * order.getPayNumber()));
                        }
                        user.setCardDayMaxNumber(product.getMonthlyNumber());
                        user.setType(3);
                    }
                    if (product.getType() == 3) {
                        //年卡
                        if(null == user.getExpirationTime()){
                            user.setExpirationTime(LocalDateTime.now().plusDays(30L * 12 * order.getPayNumber()));
                        } else if (LocalDateTime.now().compareTo(user.getExpirationTime()) < 0) {
                            user.setExpirationTime(user.getExpirationTime().plusDays(30L * 12 * order.getPayNumber()));
                        } else {
                            user.setExpirationTime(LocalDateTime.now().plusDays(30L * 12 * order.getPayNumber()));
                        }
                        user.setCardDayMaxNumber(product.getMonthlyNumber());
                        user.setType(4);
                    }
                    if (product.getType() == 4) {
                        //终身
                        if(null == user.getExpirationTime()){
                            user.setExpirationTime(LocalDateTime.now().plusDays(30L * 99999 * order.getPayNumber()));
                        } else if (LocalDateTime.now().compareTo(user.getExpirationTime()) < 0) {
                            user.setExpirationTime(user.getExpirationTime().plusDays(30L * 99999 * order.getPayNumber()));
                        } else {
                            user.setExpirationTime(LocalDateTime.now().plusDays(30L * 99999 * order.getPayNumber()));
                        }
                        user.setCardDayMaxNumber(product.getMonthlyNumber());
                        user.setType(5);
                    }
                    if (product.getType() == 5) {
                        //加油包
                        RefuelingKit kit = new RefuelingKit();
                        kit.setProductId(product.getId());
                        kit.setNumberTimes(product.getNumberTimes());
                        kit.setUserId(order.getUserId());
                        refuelingKitService.save(kit);
                    }
                    userService.saveOrUpdate(user);
                }

                if(order.getState() == 1){
                    product.setStock(product.getStock() - order.getPayNumber());
                    productService.saveOrUpdate(product);
                }
            }
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        log.info("回调处理完成！");
        return new PayCallBack("SUCCESS","处理完成");
    }

    @Override
    public B query(QueryOrderReq req) {
        Page<QueryOrderRes> page = new Page<>(req.getPageNumber(), req.getPageSize());
        Page<QueryOrderRes> queryOrderResPage = this.baseMapper.queryOrder(page, req);
        return B.okBuild(queryOrderResPage);
    }

    @Override
    public Order query(String tradeNo) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper();
        queryWrapper.eq("trade_no",tradeNo);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public B<String> aliCreateOrder(AliPayCreateReq req) throws Exception {
        PayConfig payConfig = RedisUtil.getCacheObject("payConfig");
        if(payConfig.getPayType() < 2){
            return B.finalBuild("支付宝支付通道关闭");
        }
        Product product = productService.getById(req.getProductId());
        if (null == product) {
            return B.finalBuild("商品异常");
        }
        if(product.getStock() < req.getPayNumber()){
            return B.finalBuild("库存不足");
        }
        Order order = BeanUtil.copyProperties(req, Order.class);
        order.setUserId(JwtUtil.getUserId());
        order.setPrice(product.getPrice().multiply(new BigDecimal(req.getPayNumber())));
        order.setPayType(PayWay.ALI_PAY);
        order.setCreateTime(LocalDateTime.now());
        this.save(order);
        // 生成系统订单号
        String outTradeNo = order.getId().toString();
        AlipayTradePagePayResponse response = Factory.Payment.Page()
                .pay("商品"+product.getName()+"数量"+order.getPayNumber(), outTradeNo, order.getPrice().toString(),payConfig.getAliReturnUrl());
        if (!ResponseChecker.success(response)) {
            throw new CustomException("预订单生成失败");
        }
        return B.okBuild(response.getBody().replace("\"","").replace("\n",""));
    }

    @Override
    public String aliCallBack(HttpServletRequest request) throws Exception {
        Map<String, String> stringStringMap = AliPayConfig.convertRequestParamsToMap(request);
        PayConfig payConfig = RedisUtil.getCacheObject("payConfig");
        if (Factory.Payment.Common().verifyNotify(stringStringMap)){
            if(!stringStringMap.get("app_id").equals(payConfig.getAliAppId())){
                log.info("appId不一致");
                return "failure";
            }
            AlipayNotifyParam param = AliPayConfig.buildAlipayNotifyParam(stringStringMap);
            // 支付成功
            Order order = this.getById(param.getOutTradeNo());
            if(null == order){
                log.info("订单不存在");
                return "failure";
            }else {
                if(order.getPrice().compareTo(param.getTotalAmount()) != 0){
                    log.info("订单金额异常");
                    return "failure";
                }
                order.setTradeNo(param.getTradeNo());
                order.setOperateTime(LocalDateTime.now());
                order.setState(1);
                this.saveOrUpdate(order);
            }
            return "success";
        }else {
            return "failure";
        }
    }

    public static String createSign(CreateOrderRes res){
        Map<String,String> sign = new HashMap<>();
        sign.put("pid",res.getPid().toString());
        sign.put("type",res.getType().toString());
        sign.put("out_trade_no",res.getOutTradeNo());
        sign.put("notify_url",res.getNotifyUrl());
        sign.put("return_url",res.getReturnUrl());
        sign.put("name",res.getName());
        sign.put("money",res.getMoney());
        sign = sortByKey(sign);
        //遍历map 转成字符串
        String signStr = "";
        for(Map.Entry<String,String> m :sign.entrySet()){
            signStr += m.getKey() + "=" +m.getValue()+"&";
        }
        //去掉最后一个 &
        signStr = signStr.substring(0,signStr.length()-1);
        //最后拼接上KEY
        signStr += res.getKey();
        //转为MD5
        signStr = DigestUtils.md5DigestAsHex(signStr.getBytes());
        return signStr;
    }
    public static <K extends Comparable<? super K>, V > Map<K, V> sortByKey(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();

        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByKey()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }
}
