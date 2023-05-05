package com.cn.app.chatgptbot.model.blog;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Type {
    // 主键id
    @TableId(type = IdType.AUTO)
    private Integer id;
    // 分类名
    private String name;
}
