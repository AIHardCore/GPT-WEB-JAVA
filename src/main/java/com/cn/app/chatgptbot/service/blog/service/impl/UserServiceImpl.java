package com.cn.app.chatgptbot.service.blog.service.impl;

import com.cn.app.chatgptbot.dao.blog.dao.BlogUserDao;
import com.cn.app.chatgptbot.model.blog.User;
import com.cn.app.chatgptbot.service.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private BlogUserDao blogUserDao;

    @Override
    public boolean add(User user) {
        blogUserDao.add(user);
        return true;
    }

    @Override
    public String selectByUsername(String username) {
        return blogUserDao.selectByUsername(username);
    }
}
