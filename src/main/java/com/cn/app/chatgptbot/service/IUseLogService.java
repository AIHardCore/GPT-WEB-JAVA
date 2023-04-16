package com.cn.app.chatgptbot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.model.GptKey;
import com.cn.app.chatgptbot.model.UseLog;
import com.cn.app.chatgptbot.model.base.BaseDeleteEntity;
import com.cn.app.chatgptbot.model.base.BasePageHelper;
import com.cn.app.chatgptbot.model.req.PageLogReq;
import com.cn.app.chatgptbot.model.req.ResetLogReq;
import com.cn.app.chatgptbot.model.req.UpdateLogReq;
import com.cn.app.chatgptbot.model.res.AdminHomeRes;
import org.apache.ibatis.annotations.Param;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 商品表(Product)表服务接口
 *
 * @author  
 * @since 2022-03-12 15:23:17
 */
public interface IUseLogService extends IService<UseLog> {


    Integer getDayUseNumber();

    B queryPage(BasePageHelper basePageHelper);

    B updateLog(UpdateLogReq req);

    B resetLog(ResetLogReq req);

    /**
     * 根据会话id和用户id分组查询会话列表
     * @param userId
     * @return
     */
    List<UseLog> findLogByConversationId (@Param("userId") Long userId);
}
