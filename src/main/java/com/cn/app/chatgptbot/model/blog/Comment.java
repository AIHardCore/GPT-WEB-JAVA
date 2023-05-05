package com.cn.app.chatgptbot.model.blog;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    // 主键id
    @TableId(type = IdType.AUTO)
    private Integer id;
    // 评论作者
    private String author;
    // 评论内容
    private String context;
    // 文章id
    private Integer articleId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createTime;

    // 回复对象的上一级别id
    private Integer parentId;
    // 回复对象的一级id
    private Integer topId;

    @Transient
    private String parentName;

    @Transient
    private String email;

    @Transient
    private String articleTitle;
}
