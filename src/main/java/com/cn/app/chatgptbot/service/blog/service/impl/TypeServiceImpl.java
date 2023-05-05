package com.cn.app.chatgptbot.service.blog.service.impl;

import com.cn.app.chatgptbot.dao.blog.dao.TypeDao;
import com.cn.app.chatgptbot.model.blog.Type;
import com.cn.app.chatgptbot.service.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeServiceImpl implements TypeService {
    @Autowired
    private TypeDao typeDao;

    @Override
    public List<Type> selectAll() {
        return typeDao.selectAll();
    }

    @Override
    public Type selectById(int id) {
        return typeDao.selectById(id);
    }

    @Override
    public boolean add(Type type) {
        return typeDao.add(type) > 0;
    }

    @Override
    public boolean update(Type type) {
        return typeDao.update(type) > 0;
    }

    @Override
    public boolean delete(int id) {
        return typeDao.delete(id) > 0;
    }

    @Override
    public Integer[] getArticleIdsByTypeId(int id) {
        return typeDao.selectArticleIdsByTypeId(id);
    }
}
