package com.cn.app.chatgptbot.model.wx;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信回调通知应答
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PayCallBack {

    @JsonProperty("code")
    private String code;
    @JsonProperty("message")
    private String message;


}
