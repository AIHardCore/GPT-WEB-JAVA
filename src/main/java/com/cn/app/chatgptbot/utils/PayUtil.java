package com.cn.app.chatgptbot.utils;

import com.cn.app.chatgptbot.constant.RedisKey;
import com.cn.app.chatgptbot.model.PayConfig;
import com.cn.app.chatgptbot.service.IPayConfigService;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
@Log4j2
public class PayUtil {
    @Resource
    RedisUtil redisUtil;
    @Resource
    IPayConfigService payConfigService;

    /**
     * Init.
     */
    //@PostConstruct
    public PayConfig init() {
        PayConfig  payConfig = redisUtil.getCacheObject(RedisKey.PAY_CONFIG.getName());
        if (payConfig == null){
            payConfig = payConfigService.getById(1);
            redisUtil.setCacheObject("payConfig",payConfig);
            log.info("******易支付支付信息配置完成!******");
        }
        return payConfig;
    }

}
