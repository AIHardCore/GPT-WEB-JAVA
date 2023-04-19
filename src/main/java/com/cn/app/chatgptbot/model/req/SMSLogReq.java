package com.cn.app.chatgptbot.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * ClassName:SMSLogReq
 * Package:com.cn.app.chatgptbot.model.req
 * Description:
 *
 * @Author: HardCore
 * @Create: 2023/5/19 - 05:57
 * @Version: v1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMSLogReq {

    @ApiModelProperty(value = "code")
    private String code;

    @ApiModelProperty(value = "mobile")
    @NotNull
    private String mobile;

    @ApiModelProperty(value = "state")
    private String state;
}
