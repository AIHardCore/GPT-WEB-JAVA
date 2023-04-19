package com.cn.app.chatgptbot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.Order;
import com.cn.app.chatgptbot.model.SMSLog;
import com.cn.app.chatgptbot.model.req.QueryOrderReq;
import com.cn.app.chatgptbot.model.res.AdminHomeOrder;
import com.cn.app.chatgptbot.model.res.AdminHomeOrderPrice;
import com.cn.app.chatgptbot.model.res.OrderUserRes;
import com.cn.app.chatgptbot.model.res.QueryOrderRes;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户表(SMSLog)表数据库访问层
 *
 * @author
 * @since 2022-03-12 14:35:54
 */
public interface SMSLogDao extends BaseMapper<SMSLog> {

}
