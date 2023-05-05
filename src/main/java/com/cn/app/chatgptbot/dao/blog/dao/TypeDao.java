package com.cn.app.chatgptbot.dao.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cn.app.chatgptbot.model.blog.Type;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TypeDao  extends BaseMapper<Type> {
    @Select("SELECT id, name FROM type")
    List<Type> selectAll();

    @Select("SELECT id,name FROM type WHERE id = #{id}")
    Type selectById(int id);

    @Insert("INSERT INTO type (name) VALUES (#{name})")
    int add(Type type);

    @Update("UPDATE type SET name = #{name} WHERE id = #{id}")
    int update(Type type);

    @Delete("delete from type where id=#{id};")
    int delete(int id);

    // 根据type的id查询其下所有文章的id
    @Select("select id from article where type_id=#{id};")
    Integer[] selectArticleIdsByTypeId(int id);
}
