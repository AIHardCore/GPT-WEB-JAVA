package com.cn.app.chatgptbot.service.blog.service;

import com.cn.app.chatgptbot.model.blog.Type;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface TypeService {
    List<Type> selectAll();

    Type selectById(int id);

    boolean add(Type type);

    boolean update(Type type);

    boolean delete(int id);

    // 根据type的id查询其下所有文章的id
    Integer[] getArticleIdsByTypeId(int id);
}
