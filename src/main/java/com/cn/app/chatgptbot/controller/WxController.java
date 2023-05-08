package com.cn.app.chatgptbot.controller;

import com.alibaba.fastjson.JSON;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.base.ResultEnum;
import com.cn.app.chatgptbot.model.User;
import com.cn.app.chatgptbot.model.base.WxUserLogin;
import com.cn.app.chatgptbot.model.wx.AccessTokenInfo;
import com.cn.app.chatgptbot.service.IUserService;
import com.cn.app.chatgptbot.service.IWxService;
import com.cn.app.chatgptbot.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author HardCore
 * @date 2022-03-25 16:00
 */
@Log4j2
@RestController
@RequestMapping("/wx")
@RequiredArgsConstructor
@Api(tags = {"微信API"})
public class WxController {
    @Resource
    IWxService wxService;
    @Resource
    IUserService userService;

    /**
     * 获取公众号信息
     * @return
     */
    @ApiOperation(value = "获取公众号信息",notes = "获取公众号信息")
    @RequestMapping(value = "/gzhInfo")
    private B gzhInfo(){
        return wxService.getGZHInfo();
    }

    /**
     * 获取用户的openid
     * @param userLogin
     * @return
     */
    @RequestMapping(value = "/getOpenid", method = RequestMethod.POST)
    @ApiOperation(value = "获取openid",notes = "获取openid")
    public B getOpenid(@Validated @RequestBody WxUserLogin userLogin) {
        User user = userService.getById(JwtUtil.getUserId());
        if (StringUtils.isEmpty(user.getOpenId())){
            if (StringUtils.isEmpty(userLogin.getCode())){
                return B.finalBuild("code为空，授权失败！");
            }
            log.info("微信登录：{}",userLogin.getCode());
            AccessTokenInfo accessTokenInfo = wxService.getAccessTokenInfo(userLogin.getCode());
            log.info("微信AccessToken：{}", JSON.toJSON(accessTokenInfo));
            user.setOpenId(accessTokenInfo.getOpenid());
            userService.saveOrUpdate(user);
        }
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }
}
