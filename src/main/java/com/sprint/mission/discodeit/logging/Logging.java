package com.sprint.mission.discodeit.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class Logging {

    // 접근제한자 리턴타입 패키지.클래스.메서드(파라미터) 예외
    // BinaryContent 생성 파라미터는 객체 해시코드로 찍힘
    @Pointcut(value =
            "   execution(public * com.sprint.mission.discodeit.service.UserService.*(..))" +
                    "|| execution(public * com.sprint.mission.discodeit.service.MessageService.*(..))" +
                    "|| execution(public * com.sprint.mission.discodeit.service.ChannelService.*(..))" +
                    "|| execution(public * com.sprint.mission.discodeit.controller.UserController.*(..))" +
                    "|| execution(public * com.sprint.mission.discodeit.controller.MessageController.*(..))" +
                    "|| execution(public * com.sprint.mission.discodeit.controller.ChannelController.*(..))" +
                    "|| execution(public * com.sprint.mission.discodeit.controller.BinaryContentController.download(..))" +
                    "|| execution(public * com.sprint.mission.discodeit.storage.BinaryContentStorage.download(..))")
    private void pointCut() {
    }

    @Around(value = "pointCut()")  //Throwable은 Exception의 상위 타입으로 모든 예외 처리
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Object[] args = pjp.getArgs();

        if (signature.getMethod().getName().contains("find")) {
            return pjp.proceed(args);
        }

        //패키지 경로 제외
        log.info("method={}", signature.toShortString());

        for (Object arg : args) {
            log.info("parameter={}", arg);
        }

        Object returnObject = pjp.proceed(args);
        log.info("return={}", returnObject);

        return returnObject;
    }

    @AfterThrowing(pointcut = "pointCut()", throwing = "ex")
    public void afterThrowing(JoinPoint pjp, Exception ex) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        log.error("method={}, error={}, stackTrace={}", signature.toShortString(), ex.getMessage(), ex.getStackTrace());
    }
}
