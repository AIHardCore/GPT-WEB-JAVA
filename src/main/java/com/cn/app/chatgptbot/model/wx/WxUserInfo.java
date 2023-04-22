package com.cn.app.chatgptbot.model.wx;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxUserInfo extends WxBaseEntity{
    @JsonProperty("openid")
    private String openid;
    @JsonProperty("nickname")
    private String nickname;
    @JsonProperty("sex")
    private Integer sex;
    @JsonProperty("province")
    private String province;
    @JsonProperty("city")
    private String city;
    @JsonProperty("country")
    private String country;
    @JsonProperty("headimgurl")
    private String headimgurl;
    @JsonProperty("privilege")
    private List<String> privilege;
    @JsonProperty("unionid")
    private String unionid;
}
