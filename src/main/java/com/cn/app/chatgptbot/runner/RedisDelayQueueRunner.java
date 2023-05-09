package com.cn.app.chatgptbot.runner;

import cn.hutool.extra.spring.SpringUtil;
import com.cn.app.chatgptbot.base.RedisDelayQueueEnum;
import com.cn.app.chatgptbot.handle.RedisDelayQueueHandle;
import com.cn.app.chatgptbot.utils.RedisDelayQueueUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 启动延迟队列
 */
@Slf4j
@Component
public class RedisDelayQueueRunner implements CommandLineRunner {

    @Resource
    private RedisDelayQueueUtil redisDelayQueueUtil;
    @Resource
    private ThreadPoolTaskExecutor threadPool;

    ThreadPoolExecutor executorService = new ThreadPoolExecutor(10, 50, 30, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(1000), Executors.defaultThreadFactory());
    @Override
    public void run(String... args) {
        threadPool.execute(() -> {
            while (true){
                try {
                    RedisDelayQueueEnum[] queueEnums = RedisDelayQueueEnum.values();
                    for (RedisDelayQueueEnum queueEnum : queueEnums) {
                        Object value = redisDelayQueueUtil.getDelayQueue(queueEnum.getCode());
                        if (value != null) {
                            RedisDelayQueueHandle redisDelayQueueHandle = SpringUtil.getBean(queueEnum.getBeanClass().getSimpleName());
                            redisDelayQueueHandle.execute(value);
                        }
                    }
                } catch (InterruptedException e) {
                    log.error("(Redis延迟队列异常中断) {}", e.getMessage());
                    e.printStackTrace();
                } catch (Exception e){
                    log.error("(Redis延迟队列异常中断) {}", e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        log.info("(Redis延迟队列启动成功)");
    }
}
