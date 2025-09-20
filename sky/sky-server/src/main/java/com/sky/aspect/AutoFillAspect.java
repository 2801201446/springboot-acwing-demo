package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.stereotype.Component;


import java.lang.reflect.Method;
import java.time.LocalDateTime;

//切面类，实现自动填充标识字段
/*

重点：

切面编程
+
反射使用


 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    //切入点
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillAspect()
    {}

    //前置通知，在执行操作前，操作公共字段填充
    @Before("autoFillAspect()")
    public void autoFill(JoinPoint joinPoint) throws NoSuchMethodException {
        log.info("公共字段开始填充");

        //获取当前拦截的操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//获取方法签名
        AutoFill autofill = signature.getMethod().getAnnotation(AutoFill.class);//获取方法注解对象
        OperationType operationType = autofill.value();//获取操作类型

        // 获取当前操作的实体对象
        Object[] object = joinPoint.getArgs();
        if(object==null || object.length==0)
        {
            return;
        }

        //获取第一个实体
        Object o = object[0];
        //准备将操作的数据
        LocalDateTime now=LocalDateTime.now();
        Long currid=BaseContext.getCurrentId();
        //进行对象属性的操作，通过反射进行赋值
        if(operationType==OperationType.INSERT)
        {
            try {
            Method setCreateUser = o.getClass().getDeclaredMethod("setCreateUser", Long.class);
            Method setCreateTime = o.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
            Method setUpdateUser = o.getClass().getDeclaredMethod("setUpdateUser", Long.class);
            Method setUpdateTime = o.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);

            //通过反射赋值
            setCreateUser.invoke(o,currid);
            setCreateTime.invoke(o,now);
            setUpdateTime.invoke(o,now);
            setUpdateUser.invoke(o,currid);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }else
        {
            try {
                Method setUpdateUser = o.getClass().getDeclaredMethod("setUpdateUser", Long.class);
                Method setUpdateTime = o.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);

                setUpdateTime.invoke(o,now);
                setUpdateUser.invoke(o,currid);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
