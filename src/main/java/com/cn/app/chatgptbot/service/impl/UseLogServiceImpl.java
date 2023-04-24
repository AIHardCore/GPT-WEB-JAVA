package com.cn.app.chatgptbot.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.base.ResultEnum;
import com.cn.app.chatgptbot.dao.UseLogDao;
import com.cn.app.chatgptbot.model.UseLog;
import com.cn.app.chatgptbot.model.User;
import com.cn.app.chatgptbot.model.base.BasePageHelper;
import com.cn.app.chatgptbot.model.req.PageLogReq;
import com.cn.app.chatgptbot.model.req.ResetLogReq;
import com.cn.app.chatgptbot.model.req.UpdateLogReq;
import com.cn.app.chatgptbot.service.IUseLogService;
import com.cn.app.chatgptbot.service.IUserService;
import com.cn.app.chatgptbot.utils.JwtUtil;
import javax.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 用户表(Product)表服务实现类
 *
 * @author  
 * @since 2022-03-12 15:23:55
 */
@Service("UseLogService")
@Transactional(rollbackFor = Exception.class)
public class UseLogServiceImpl extends ServiceImpl<UseLogDao, UseLog> implements IUseLogService {

    @Resource
    @Lazy
    IUserService userService;
    @Override
    public Integer getDayUseNumber() {
        return this.baseMapper.getDayUseNumber(JwtUtil.getUserId());
    }

    @Override
    public Integer getDayUseNumber(Long userId) {
        return this.baseMapper.getDayUseNumber(userId);
    }

    @Override
    public B queryPage(BasePageHelper basePageHelper) {
        JSONObject jsonObject = new JSONObject();
        Page<UseLog> page = new Page<>(basePageHelper.getPageNumber(),basePageHelper.getPageSize());
        QueryWrapper<UseLog> queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id",JwtUtil.getUserId());
        queryWrapper.eq("conversation_id",basePageHelper.getConversationId());
        queryWrapper.eq("state",0);
        queryWrapper.orderByDesc("create_time");
        Page<UseLog> useLogPage = baseMapper.selectPage(page, queryWrapper);
        Collections.reverse(useLogPage.getRecords());
        jsonObject.put("logPage",useLogPage);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);
    }

    @Override
    public B updateLog(UpdateLogReq req) {
        UseLog useLog = this.getById(req.getLogId());
        if(null == useLog){
            return B.okBuild();
        }
        useLog.setUseValue(req.getNewMessages());
        this.saveOrUpdate(useLog);
        return B.okBuild();
    }

    @Override
    public B resetLog(ResetLogReq req) {
        UseLog useLog = this.getById(req.getLogId());
        if(null == useLog || useLog.getState() == 1){
            return B.okBuild();
        }
        useLog.setState(1);
        useLog.setUseValue(req.getNewMessages());
        if(useLog.getUseType() == 1){
            User user = this.userService.getById(JwtUtil.getUserId());
            user.setRemainingTimes(user.getRemainingTimes() + 1);
            this.userService.saveOrUpdate(user);
        }
        this.saveOrUpdate(useLog);
        return B.okBuild();
    }

    /**
     * 用户id分组查询会话列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<UseLog> findLogByConversationId(Long userId) {
        return this.baseMapper.findLogByConversationId(userId);
    }


}
