package com.cn.app.chatgptbot.model.base;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author
 * @date 2023-04-21 02:01
 */
@Data
@ApiModel(value = "微信用户登录对象")
public class WxUserLogin {
    /**
     * 用户code
     */
    private String code;
}
