package com.cn.app.chatgptbot.runner;

import com.cn.app.chatgptbot.constant.RedisKey;
import com.cn.app.chatgptbot.model.ali.sms.SMSConfig;
import com.cn.app.chatgptbot.utils.RedisUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 阿里云配置初始化
 * ClassName:AliSMSConfig
 * Package:com.cn.app.chatgptbot.config.ali
 * Description:
 *
 * @Author: Hardcore
 * @Create: 2023/5/19 - 05:10
 * @Version: v1.0
 */
@Component
@Log4j2
public class AliSMSConfigRunner implements ApplicationRunner {

    @Resource
    RedisUtil redisUtil;
    @Resource
    SMSConfig smsConfig;


    @Override
    public void run(ApplicationArguments args){
        //初始化阿里云短信配置并保存到redis中
        redisUtil.setCacheObject(RedisKey.AILYUN_SMS_CONFIG.getName(),smsConfig);
        log.info("******初始化阿里云短信配置完成!******");
    }
}
