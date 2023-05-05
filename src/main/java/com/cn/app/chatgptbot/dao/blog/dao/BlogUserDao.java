package com.cn.app.chatgptbot.dao.blog.dao;

import com.cn.app.chatgptbot.model.blog.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BlogUserDao {

    @Select("select password from tb_user where username = #{username};")
    String selectByUsername(String username);

    @Insert("insert into tb_user values (#{id},#{username},#{password});")
    void add(User user);
}
