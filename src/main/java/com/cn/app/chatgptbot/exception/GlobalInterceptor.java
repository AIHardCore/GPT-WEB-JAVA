package com.cn.app.chatgptbot.exception;

import com.cn.app.chatgptbot.base.B;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


/**
 * The type Global interceptor.
 *
 * @author bdth
 * @email 2074055628@qq.com
 */
@RestControllerAdvice
@SuppressWarnings("all")
@Slf4j
public class GlobalInterceptor {


    /**
     * Exception handler result.
     *
     * @param e the e
     * @return the result
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public B exceptionHandler(Exception e) {
        //interceptRequestParameterError
        if (e instanceof MethodArgumentNotValidException) {
            //getFirst
            final List<ObjectError> allErrors = ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors();
            return B.finalBuild(allErrors.get(0).getDefaultMessage());
            //return Result.error(allErrors.get(0).getDefaultMessage());
        }
        //blockCustomErrors
        if (e instanceof CustomException) {
            return B.finalBuild(e.getMessage());
            //return Result.error(e.getMessage());
        }
        return B.finalBuild(e.getMessage());
        //return Result.error(e.getMessage());
    }


}
