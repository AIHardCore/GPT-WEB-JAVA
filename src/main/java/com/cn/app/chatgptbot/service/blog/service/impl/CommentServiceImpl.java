package com.cn.app.chatgptbot.service.blog.service.impl;

import com.cn.app.chatgptbot.dao.blog.dao.CommentDao;
import com.cn.app.chatgptbot.model.blog.Comment;
import com.cn.app.chatgptbot.service.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentDao commentDao;

    @Override
    public Integer commentCount(Integer start, Integer pageSize, Comment comment) {
        return commentDao.commentCount(start, pageSize, comment);
    }

    @Override
    public List<Comment> selectByPage(Integer start, Integer pageSize, Comment comment) {
        return commentDao.selectByPage(start, pageSize, comment);
    }

    @Override
    public List<Comment> selectAllByArticleId(Integer articleId) {
        return commentDao.selectAllByArticleId(articleId);
    }

    @Override
    public List<Comment> selectByTopId(Integer articleId, Integer topId) {
        return commentDao.selectByTopId(articleId, topId);
    }

    @Override
    public boolean addComment(Comment comment) {
        return commentDao.addComment(comment) > 0;
    }

    @Override
    public boolean delete(Integer id) {
        return commentDao.delete(id) > 0;
    }
}
