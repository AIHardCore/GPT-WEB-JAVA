package com.cn.app.chatgptbot.flow.server;

import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketServerQueue {
    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。
     */
    public static ConcurrentHashMap<Long, ChatWebSocketServer> chatWebSocketMap = new ConcurrentHashMap<>(200);
}
