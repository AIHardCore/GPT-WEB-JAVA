package com.cn.app.chatgptbot.controller;

import com.cn.app.chatgptbot.base.B;
import com.cn.app.chatgptbot.service.IWxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author HardCore
 * @date 2022-03-25 16:00
 */
@RestController
@RequestMapping("/wx")
@RequiredArgsConstructor
@Api(tags = {"微信API"})
public class WxController {
    @Resource
    IWxService wxService;

    /**
     * 获取公众号信息
     * @return
     */
    @ApiOperation(value = "获取公众号信息",notes = "获取公众号信息")
    @RequestMapping(value = "/gzhInfo")
    private B gzhInfo(){
        return wxService.getGZHInfo();
    }
}
