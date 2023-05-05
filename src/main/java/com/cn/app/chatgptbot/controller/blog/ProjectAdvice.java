package com.cn.app.chatgptbot.controller.blog;

import com.cn.app.chatgptbot.controller.blog.result.UserResult;
import com.cn.app.chatgptbot.exception.BusinessException;
import com.cn.app.chatgptbot.exception.SystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectAdvice {
    @ExceptionHandler(SystemException.class)
    public UserResult doSystemException(SystemException e) {
        return new UserResult(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public UserResult doBusinessException(BusinessException e) {
        return new UserResult(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public UserResult doException(Exception e) {
        e.printStackTrace();
        return new UserResult(Code.SYSTEM_UNKNOWN_ERR, "服务器繁忙，请稍后再试！");
    }
}
