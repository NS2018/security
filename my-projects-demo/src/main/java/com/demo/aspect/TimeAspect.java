package com.demo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 切片 (filter and interceptor component)
 */
@Aspect
@Component
public class TimeAspect {

    /**
     * pjp -- 拦截方法的信息
     * @param pjp
     * @return
     */
    @Around("execution(* com.demo.controller.UserController.*(..))")
    public Object handlerControllerMethod(ProceedingJoinPoint pjp) throws Throwable {

        System.out.println("time aspect start");
        Object[] args = pjp.getArgs();
        for (Object arg : args) {
            System.out.println("arg:"+arg);
        }
        long start = new Date().getTime();
        //与chain.dofilter一致
        Object result = pjp.proceed();
        long end = new Date().getTime();
        System.out.println("time aspect end");
        System.out.println("time aspect spend :"+(end - start));
        return result;
    }

}
