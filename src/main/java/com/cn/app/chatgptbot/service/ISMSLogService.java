package com.cn.app.chatgptbot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.SMSLog;
import com.cn.app.chatgptbot.model.req.*;

/**
 * 短信日志(SMSLog)表服务接口
 *
 * @author  HardCore
 * @since 2022-03-12 15:23:17
 */
public interface ISMSLogService extends IService<SMSLog> {

    /**
     * 根据手机号查询
     * @param mobile
     * @return
     */
    SMSLog findByMobile(String mobile);

    /**
     * 根据手机号和验证码查询未使用的验证码
     * @param mobile
     * @param code
     * @return
     */
    SMSLog findUnUseByMobileAndCode(String mobile, String code);

    /**
     * 发送验证码
     * @param mobile
     * @param code
     * @return
     */
    B send(String mobile, String code);

    /**
     * 发送验证码
     * @param smsLog
     * @return
     */
    B send(SMSLog smsLog);

    /**
     * 发送注册验证码
     * @param mobile
     * @return
     */
    B sendRegisterSMS(String mobile);

}
