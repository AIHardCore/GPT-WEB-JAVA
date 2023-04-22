package com.cn.app.chatgptbot.model.wx;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信用户信息
 */
@NoArgsConstructor
@Data
public class AccessTokenInfo extends WxBaseEntity{
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private Integer expiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("openid")
    private String openid;
    @JsonProperty("scope")
    private String scope;
    @JsonProperty("is_snapshotuser")
    private Integer isSnapshotuser;
    @JsonProperty("unionid")
    private String unionid;
}
