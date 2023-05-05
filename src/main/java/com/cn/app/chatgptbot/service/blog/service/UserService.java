package com.cn.app.chatgptbot.service.blog.service;

import com.cn.app.chatgptbot.model.blog.User;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserService {

    /**
     * 添加博主账户
     */
    boolean add(User user);

    /**
     * 登录博主账户
     */
    String selectByUsername(String username);
}
