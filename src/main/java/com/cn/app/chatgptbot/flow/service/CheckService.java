package com.cn.app.chatgptbot.flow.service;

import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.base.Result;
import com.cn.app.chatgptbot.constant.CommonConst;
import com.cn.app.chatgptbot.model.GptKey;
import com.cn.app.chatgptbot.model.UseLog;
import com.cn.app.chatgptbot.model.User;
import com.cn.app.chatgptbot.model.res.UserInfoRes;
import com.cn.app.chatgptbot.service.*;
import com.cn.app.chatgptbot.utils.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ClassName:service
 * Package:com.cn.app.chatgptbot.flow
 * Description:
 *
 * @Author: ShenShiPeng
 * @Create: 2023/4/4 - 10:03
 * @Version: v1.0
 */
@Service("CheckService")
public class CheckService {

    @Resource
    IGptKeyService gptKeyService;
    @Resource
    IUserService userService;

    @Resource
    IRefuelingKitService refuelingKitService;

    @Resource
    IUseLogService useLogService;

    @Resource
    AsyncLogService asyncLogService;

    @Transactional(rollbackFor = Exception.class)
    public Result checkUser(String mainKey, Long userId, Session session,User user) throws IOException {
        String redisToken  = RedisUtil.getCacheObject(CommonConst.REDIS_KEY_PREFIX_TOKEN + userId);
        if(StringUtils.isEmpty(redisToken)){
            //session.getBasicRemote().sendText("请先登录");
            return Result.error("请先登录");
        }
        List<GptKey> gptKeyList = gptKeyService.lambdaQuery().eq(GptKey::getGptKey, mainKey).last("limit 1").list();
        if(null == gptKeyList || gptKeyList.size() == 0){
            //session.getBasicRemote().sendText("Key 异常 请稍后重试");
            return Result.error("Key 异常 请稍后重试");
        }
        //查询当前用户信息
        UseLog useLog = new UseLog();
        useLog.setGptKey(mainKey);
        useLog.setUserId(userId);
        BeanUtils.copyProperties(userService.getById(userId),user);
        if (user.getDeleted()){
            return Result.error("用户状态异常，请稍后重试");
        }
        B<UserInfoRes> userInfo = userService.getType(userId);
        Integer type = userInfo.getData().getType();
        if (type != -1){
            if(type == 1){
                //判断剩余次数
                if(user.getRemainingTimes() < 1){
                    //session.getBasicRemote().sendText("${<INSUFFICIENT_FREQUENCY>}");
                    return Result.error("剩余次数不足,请充值");
                }
                //是否已达今日已达上线
                Integer dayUseNumber = useLogService.getDayUseNumber(userId);
                if((dayUseNumber + 1) > user.getCardDayMaxNumber()){
                    return Result.error("当日已超过最大访问次数");
                }
                useLog.setUseType(1);
                user.setRemainingTimes(user.getRemainingTimes() - 1);
            }else {
                //套餐用户 先查询是否有可用的加油包
                Long userKitId = refuelingKitService.getUserKitId(userId);
                if(userKitId > 0){
                    useLog.setKitId(userKitId);
                    useLog.setUseType(1);
                }else {
                    //判断套餐是否到期
                    if(user.getExpirationTime() != null && user.getExpirationTime().compareTo(LocalDateTime.now()) < 0) {
                        //次数用户 查询用户次数
                        if (user.getRemainingTimes() < 1) {
                            //session.getBasicRemote().sendText("月卡过期或当日已超过最大访问次数");
                            return Result.error("剩余次数不足,请充值");
                        }
                        //是否已达今日已达上线
                        Integer dayUseNumber = useLogService.getDayUseNumber(userId);
                        if ((dayUseNumber + 1) > 5) {
                            //session.getBasicRemote().sendText("月卡过期或当日已超过最大访问次数");
                            return Result.error("当日已超过最大访问次数");
                        }
                        useLog.setUseType(1);
                        user.setRemainingTimes(user.getRemainingTimes() - 1);
                    }else if(user.getExpirationTime() != null && user.getExpirationTime().compareTo(LocalDateTime.now()) > 0){
                        //是否已达今日已达上线
                        Integer dayUseNumber = useLogService.getDayUseNumber(userId);
                        if((dayUseNumber + 1) > user.getCardDayMaxNumber()){
                            //session.getBasicRemote().sendText("月卡过期或当日已超过最大访问次数");
                            return Result.error("当日已超过最大访问次数");
                        }
                        useLog.setUseType(2);
                    }else { //套餐到期消耗使用次数
                        //是否已达今日已达上线
                        Integer dayUseNumber = useLogService.getDayUseNumber(userId);
                        if((dayUseNumber + 1) > 5){
                            //session.getBasicRemote().sendText("月卡过期或当日已超过最大访问次数");
                            return Result.error("当日已超过最大访问次数");
                        }else {
                            useLog.setUseType(2);
                            user.setRemainingTimes(user.getRemainingTimes() - 1);
                        }
                    }
                }
            }
        }
        GptKey gptKey = gptKeyList.get(0);
        gptKey.setUseNumber(gptKey.getUseNumber()+1);
        gptKey.setOperateTime(LocalDateTime.now());
        asyncLogService.saveKeyLog(gptKey);
        return Result.data(useLog);
    }
}
