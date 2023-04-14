package com.cn.app.chatgptbot.config.task;

import com.cn.app.chatgptbot.flow.server.ChatWebSocketServer;
import com.cn.app.chatgptbot.flow.server.ChatWebSocketServerQueue;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

/**
 * @Auther: lanhaifeng
 * @Date: 2019/5/16 0016 14:29
 * @Description:webSocket定时发送消息类
 * @statement: 以<60s的频率发送给websocket连接的对象，以防止反向代理的60s超时限制
 */
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class SaticScheduleTask {

    @Resource
    private ChatWebSocketServer webSocketServer;


    //3.添加定时任务
    //@Scheduled(cron = "0/5 * * * * ?")
    //或直接指定时间间隔，例如：5秒
    @Scheduled(fixedRate=5*1000)
    private void configureTasks() throws Exception{
        for (Long key : ChatWebSocketServerQueue.chatWebSocketMap.keySet()){
            ChatWebSocketServerQueue.chatWebSocketMap.get(key).sendMessage("${<keep>}");
        }
    }
}
