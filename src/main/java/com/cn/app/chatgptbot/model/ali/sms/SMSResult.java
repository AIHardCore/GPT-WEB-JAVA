package com.cn.app.chatgptbot.model.ali.sms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SMSResult {

    @JsonProperty("Code")
    private String code;
    @JsonProperty("Message")
    private String message;
    @JsonProperty("BizId")
    private String bizId;
    @JsonProperty("RequestId")
    private String requestId;
}
