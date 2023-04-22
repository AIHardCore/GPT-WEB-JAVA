package com.cn.app.chatgptbot.runner;

import com.cn.app.chatgptbot.constant.RedisKey;
import com.cn.app.chatgptbot.model.ali.sms.SMSConfig;
import com.cn.app.chatgptbot.model.wx.WxGZH;
import com.cn.app.chatgptbot.model.wx.WxPay;
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
public class WxConfigRunner implements ApplicationRunner {

    @Resource
    RedisUtil redisUtil;
    @Resource
    WxPay wxPay;
    @Resource
    WxGZH wxGZH;


    @Override
    public void run(ApplicationArguments args){
        //初始化微信开发信息配置并保存到redis中
        redisUtil.setCacheObject(RedisKey.WX_PAY.getName(),wxPay);
        redisUtil.setCacheObject(RedisKey.WX_GZH.getName(),wxGZH);
        log.info("******微信开发信息配置完成!******");
    }
}
