package com.cn.app.chatgptbot.controller;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.base.ResultEnum;
import com.cn.app.chatgptbot.config.AvoidRepeatRequest;
import com.cn.app.chatgptbot.constant.CommonConst;
import com.cn.app.chatgptbot.model.User;
import com.cn.app.chatgptbot.model.base.UserLogin;
import com.cn.app.chatgptbot.model.base.WxUserLogin;
import com.cn.app.chatgptbot.model.req.RegisterReq;
import com.cn.app.chatgptbot.model.res.AdminHomeRes;
import com.cn.app.chatgptbot.model.res.UserInfoRes;
import com.cn.app.chatgptbot.model.wx.AccessTokenInfo;
import com.cn.app.chatgptbot.model.wx.WxUserInfo;
import com.cn.app.chatgptbot.service.IUserService;
import com.cn.app.chatgptbot.service.IWxService;
import com.cn.app.chatgptbot.utils.JwtUtil;
import com.cn.app.chatgptbot.utils.RedisUtil;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author
 * @date 2022-03-25 16:00
 */
@Log4j2
@RestController
@RequestMapping("/user/token")
@RequiredArgsConstructor
@Api(tags = {"用户/管理员登录、注册，首页，获取用户类型"})
public class UserTokenController {


    final IUserService userService;

    final RedisUtil redisUtil;

    final IWxService wxService;


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation(value = "用户登录")
    //@AvoidRepeatRequest(intervalTime = 60 * 3L ,msg = "请勿短时间连续登录")
    public B<JSONObject> userLogin(@Validated @RequestBody UserLogin userLogin) {
        List<User> list = userService.lambdaQuery()
                .eq(User::getMobile, userLogin.getMobile())
                .eq(User::getPassword, userLogin.getPassword())
                .ne(User::getType,-1)
                .list();
        if (list == null || list.size() == 0) {
            return B.finalBuild("用户名或密码错误");
        }
        User user = list.get(0);
        JSONObject jsonObject = new JSONObject();
        //生成token
        String token = JwtUtil.createToken(user);
        jsonObject.put("token", token);
        jsonObject.put("userId", user.getId());
        jsonObject.put("name", user.getName());
        jsonObject.put("type", user.getType());
        jsonObject.put("expirationTime", user.getExpirationTime());
        User nweUser = new User();
        nweUser.setId(user.getId());
        nweUser.setLastLoginTime(LocalDateTime.now());
        jsonObject.put("lastLoginTime", null == user.getLastLoginTime() ? nweUser.getLastLoginTime() : user.getLastLoginTime());
        userService.updateById(nweUser);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);

    }

    @RequestMapping(value = "/wxlogin", method = RequestMethod.POST)
    @ApiOperation(value = "用户登录")
    public B<JSONObject> wxlogin(@Validated @RequestBody WxUserLogin wxUserLogin) {
        if (StringUtils.isEmpty(wxUserLogin.getCode())){
            return B.finalBuild("code为空，授权失败！");
        }
        log.info("微信登录：{}",wxUserLogin.getCode());
        User user = null;
        AccessTokenInfo accessTokenInfo = wxService.getAccessTokenInfo(wxUserLogin.getCode());
        log.info("微信AccessToken：{}", JSON.toJSON(accessTokenInfo));
        List<User> list = userService.lambdaQuery()
                .eq(User::getOpenId, accessTokenInfo.getOpenid())
                .ne(User::getType,-1)
                .list();
        if (list == null || list.size() == 0) {
            WxUserInfo userInfo = wxService.getUserInfo(accessTokenInfo.getAccessToken(),accessTokenInfo.getOpenid());
            log.info("微信用户信息：{}", JSON.toJSON(userInfo));
            user = userService.register(userInfo);
        }else {
            user = list.get(0);
            log.info("用户信息：{}", JSON.toJSON(user));
        }
        JSONObject jsonObject = new JSONObject();
        //生成token
        String token = JwtUtil.createToken(user);
        jsonObject.put("token", token);
        jsonObject.put("userId", user.getId());
        jsonObject.put("name", user.getName());
        jsonObject.put("type", user.getType());
        jsonObject.put("expirationTime", user.getExpirationTime());
        User nweUser = new User();
        nweUser.setId(user.getId());
        nweUser.setLastLoginTime(LocalDateTime.now());
        jsonObject.put("lastLoginTime", null == user.getLastLoginTime() ? nweUser.getLastLoginTime() : user.getLastLoginTime());
        userService.updateById(nweUser);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ApiOperation(value = "注册")
    @AvoidRepeatRequest(msg = "请勿短时间内重复注册")
    public B register(@Validated @RequestBody RegisterReq req) {
        return userService.register(req);
    }

    @RequestMapping(value = "/home", method = RequestMethod.POST)
    @ApiOperation(value = "首页信息")
    public B<UserInfoRes> home() {
        return userService.home();
    }

    @RequestMapping(value = "/getType", method = RequestMethod.POST)
    @ApiOperation(value = "查询用户类型")
    public B<UserInfoRes> getType() {
        return userService.getType();
    }


    @RequestMapping(value = "/admin/login", method = RequestMethod.POST)
    @ApiOperation(value = "用户登录")
    public B<JSONObject> adminLogin(@Validated @RequestBody UserLogin userLogin) {
        List<User> list = userService.lambdaQuery()
                .eq(User::getMobile, userLogin.getMobile())
                .eq(User::getPassword, userLogin.getPassword())
                .eq(User::getType, -1)
                .list();
        if (list == null || list.size() == 0) {
            return B.finalBuild("用户名或密码错误");
        }
        User user = list.get(0);
        JSONObject jsonObject = new JSONObject();
        //生成token
        String token = JwtUtil.createToken(user);
        jsonObject.put("token", token);
        jsonObject.put("userId", user.getId());
        jsonObject.put("name", user.getName());
        jsonObject.put("type", user.getType());
        jsonObject.put("expirationTime", user.getExpirationTime());
        User nweUser = new User();
        nweUser.setId(user.getId());
        nweUser.setLastLoginTime(LocalDateTime.now());
        jsonObject.put("lastLoginTime", null == user.getLastLoginTime() ? nweUser.getLastLoginTime() : user.getLastLoginTime());
        userService.updateById(nweUser);
        redisUtil.setCacheObject(CommonConst.REDIS_KEY_PREFIX_TOKEN + user.getId(), SecureUtil.md5(token),
                CommonConst.TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        return B.build(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), jsonObject);

    }

    @RequestMapping(value = "/admin/home", method = RequestMethod.POST)
    @ApiOperation(value = "管理端首页信息")
    public B<AdminHomeRes> adminHome() {
        return userService.adminHome();
    }

}
