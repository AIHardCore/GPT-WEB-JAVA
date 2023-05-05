package com.cn.app.chatgptbot.service.blog.service.impl;

import com.cn.app.chatgptbot.dao.blog.dao.TagDao;
import com.cn.app.chatgptbot.model.blog.Tag;
import com.cn.app.chatgptbot.service.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagDao tagDao;

    @Override
    public List<Tag> selectAll() {
        return tagDao.selectAll();
    }

    @Override
    public Tag getById(int id) {
        return tagDao.selectById(id);
    }

    @Override
    public boolean add(Tag tag) {
        return tagDao.add(tag) > 0;
    }

    @Override
    public boolean update(Tag tag) {
        return tagDao.update(tag) > 0;
    }

    @Override
    public boolean delete(int id) {
        return tagDao.delete(id) > 0;
    }

    @Override
    public Integer[] getArticleIdsByTagId(int id) {
        return tagDao.selectArticleIdsByTagId(id);
    }
}
