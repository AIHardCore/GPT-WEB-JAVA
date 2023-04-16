package com.cn.app.chatgptbot.model.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * ClassName:ResetLogReq
 * Package:com.cn.app.chatgptbot.model.req
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/3/24 - 22:39
 * @Version: v1.0
 */
@Data
public class PageLogReq {

    @NotNull(message = "会话id不能为空")
    private String conversationId;

}
