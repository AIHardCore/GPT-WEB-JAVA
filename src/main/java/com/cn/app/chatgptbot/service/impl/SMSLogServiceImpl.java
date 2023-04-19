package com.cn.app.chatgptbot.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponseBody;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.constant.CommonConst;
import com.cn.app.chatgptbot.dao.SMSLogDao;
import com.cn.app.chatgptbot.model.SMSLog;
import com.cn.app.chatgptbot.service.ISMSLogService;
import com.cn.app.chatgptbot.utils.AliSMSUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service("smsLogService")
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class SMSLogServiceImpl extends ServiceImpl<SMSLogDao, SMSLog> implements ISMSLogService {

    /**
     * 根据手机号查询
     * @param mobile
     * @return
     */
    @Override
    public SMSLog findByMobile(String mobile){
        List<SMSLog> smsLogs = this.lambdaQuery()
                .eq(SMSLog::getMobile,mobile)
                .ge(SMSLog::getExpirationTime, LocalDateTime.now())
                .orderByDesc(SMSLog::getCreateTime)
                .last("limit 1").list();
        if (smsLogs == null || smsLogs.size() == 0){
            return null;
        }
        return smsLogs.get(0);
    }

    /**
     * 根据手机号和验证码查询
     *
     * @param mobile
     * @param code
     * @return
     */
    @Override
    public SMSLog findUnUseByMobileAndCode(String mobile, String code) {
        List<SMSLog> smsLogs = this.lambdaQuery()
                .eq(SMSLog::getMobile,mobile)
                .eq(SMSLog::getCode,code)
                .eq(SMSLog::getState,0)
                .ge(SMSLog::getExpirationTime, LocalDateTime.now())
                .orderByDesc(SMSLog::getCreateTime)
                .last("limit 1").list();
        if (smsLogs == null || smsLogs.size() == 0){
            return null;
        }
        return smsLogs.get(0);
    }

    /**
     * 发送验证码
     *
     * @param mobile
     * @param code
     * @return
     */
    @Override
    public B send(String mobile, String code) {
        SendSmsResponseBody body = AliSMSUtil.send(mobile,code);
        if (CommonConst.OK.equals(body.getCode())){
            SMSLog smsLog = SMSLog.builder()
                    .BizId(body.getBizId())
                    .RequestId(body.getRequestId())
                    .expirationTime(DateUtil.offset(new Date(), DateField.MINUTE,5).toTimestamp().toLocalDateTime())
                    .code(code)
                    .mobile(mobile)
                    .state(0)
                    .build();
            this.save(smsLog);
            return B.okBuild();
        }else {
            if ("isv.DAY_LIMIT_CONTROL".equals(body.getMessage())){
                return B.finalBuild("操作太频繁了,请稍后再试！");
            }
            switch (body.getCode()){
                case "isv.DAY_LIMIT_CONTROL":
                    log.warn(String.format("手机号：%s,触发日发送限额",mobile));
                    return B.finalBuild("操作太频繁了,请稍后再试！");
                case "isv.BUSINESS_LIMIT_CONTROL":
                    log.warn(String.format("手机号：%s,触发调用频率限额(触发云通信流控限制)",mobile));
                    return B.finalBuild("操作太频繁了,请稍后再试！");
            }
            return B.finalBuild("服务器开小差了,请稍后再试！");
        }
    }

    /**
     * 发送验证码
     *
     * @param smsLog
     * @return
     */
    @Override
    public B send(SMSLog smsLog) {
        SendSmsResponseBody body = AliSMSUtil.send(smsLog.getMobile(),smsLog.getCode());
        if (body.getCode() == CommonConst.OK){
            this.saveOrUpdate(smsLog);
            return B.okBuild(smsLog.getCode());
        }else {
            return B.finalBuild("服务器开小差了,请稍后再试！");
        }
    }

    /**
     * 发送注册验证码
     *
     * @param mobile
     * @return
     */
    @Override
    public B sendRegisterSMS(String mobile) {
        if (StringUtils.isEmpty(mobile)){
            log.error("手机号不玩为空！");
            return B.finalBuild("服务器开小差了,请稍后再试！！");
        }
        SMSLog smsLog = findByMobile(mobile);
        if (smsLog != null && smsLog.getState() == 0){
            smsLog.setExpirationTime(DateUtil.offset(new Date(), DateField.MINUTE,5).toTimestamp().toLocalDateTime());
            return send(mobile,smsLog.getCode());
        }else {
            return send(mobile,RandomUtil.randomNumbers(6));
        }
    }
}
