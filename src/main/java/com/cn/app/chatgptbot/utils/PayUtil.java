package com.cn.app.chatgptbot.utils;

import com.cn.app.chatgptbot.constant.RedisKey;
import com.cn.app.chatgptbot.model.PayConfig;
import com.cn.app.chatgptbot.service.IPayConfigService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class PayUtil {
    @Resource
    RedisUtil redisUtil;
    @Resource
    IPayConfigService payConfigService;

    public PayConfig init() {
        PayConfig  payConfig = redisUtil.getCacheObject(RedisKey.PAY_CONFIG.getName());
        if (payConfig == null){
            payConfig = payConfigService.getById(1);
            redisUtil.setCacheObject("payConfig",payConfig);
        }
        return payConfig;
    }

}
