package com.yuxin.messaging.aspect;

import com.yuxin.messaging.model.User;
import com.yuxin.messaging.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Log4j2
@Order(0)
public class AuthenticationAspect {
    @Autowired
    UserService userService;

    @Around("execution(* com.yuxin.messaging.controller.*.*(..))" +
            "&& @annotation(com.yuxin.messaging.annotation.NeedAuthentication)")
    public Object authenticate(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("Authenticating...");
        HttpServletRequest httpServletRequest =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String loginToken = httpServletRequest.getHeader("Login-Token");
        User user = this.userService.authenticate(loginToken);
        var args = proceedingJoinPoint.getArgs();
        args[0] = user;
        return proceedingJoinPoint.proceed(args);
    }
}
