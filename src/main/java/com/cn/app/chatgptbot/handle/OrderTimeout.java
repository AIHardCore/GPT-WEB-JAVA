package com.cn.app.chatgptbot.handle;

import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.base.ResultEnum;
import com.cn.app.chatgptbot.model.Order;
import com.cn.app.chatgptbot.service.IOrderService;
import com.cn.app.chatgptbot.service.IWxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 订单支付超时处理类
 */
@Component("OrderTimeout")
@Slf4j
public class OrderTimeout implements RedisDelayQueueHandle<Map> {
    @Resource
    IOrderService orderService;
    @Resource
    IWxService wxService;

    @Override
    public void execute(Map map) {
        log.info("(收到超时订单延迟消息) {}", map);
        // TODO 订单相关，处理业务逻辑...
        Order order = orderService.query(map.get("tradeNo").toString());
        if (order != null && order.getState().equals(0)){
            try {
                B b = wxService.close(order.getTradeNo());
                if (ResultEnum.SUCCESS.getCode().equals(b.getStatus())){
                    order.setState(2);
                    order.setOperateTime(LocalDateTime.now());
                    orderService.saveOrUpdate(order);
                    log.info("订单超时自动关闭:{}", order.getTradeNo());
                }else {
                    log.error(b.getMessage());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //1.调用第三方（微信，支付宝）的支付接口，查询订单是否已经支付，如果确认没支付则，调用关闭订单支付的api,并修改订单的状态为关闭，同时回滚库存数量。
        //2.如果支付状态为已支付则需要做补偿操作，修改订单的状态为已支付，订单历史记录

    }
}
