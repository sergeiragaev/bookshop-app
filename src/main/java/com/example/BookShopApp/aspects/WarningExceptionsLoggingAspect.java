package com.example.BookShopApp.aspects;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Aspect
@Component
public class WarningExceptionsLoggingAspect {
        private final Logger logger = Logger.getLogger(this.getClass().getName());
        @Pointcut(value = "@annotation(com.example.BookShopApp.annotations.WarningExceptionsLoggable)")
        public void exceptionsCatcherPointcut(){}
        @AfterThrowing(pointcut = "exceptionsCatcherPointcut()", throwing = "e")
        public void exceptionsCatcherAdvice(Exception e){
                logger.warning(e.getLocalizedMessage());
        }
}
