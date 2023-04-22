package com.cn.app.chatgptbot.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cn.app.chatgptbot.model.base.BaseEntity;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 短信日志表(SMSLog)实体类
 *
 * @author
 * @since 2023-05-19 05:37:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("ali_sms_log")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SMSLog extends BaseEntity implements Serializable {
    /**
     * 验证码
     */
    private String code;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 状态，0：未使用，1：已使用
     */
    private int state;

    /**
     * 发送回执ID，可根据发送回执ID在接口QuerySendDetails中查询具体的发送状态
     */
    private String BizId;

    /**
     * 请求ID
     */
    private String RequestId;

    /**
     * 过期时间
     */
    private LocalDateTime expirationTime;
}
