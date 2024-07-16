package com.sparta.sensorypeople.common.redisson;

import com.sparta.sensorypeople.domain.board.entity.Board;
import com.sparta.sensorypeople.domain.column.dto.ColumnResponseDto;
import com.sparta.sensorypeople.domain.column.entity.Columns;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/*
AOP를 활용한 분산락 적용을 구현하려 했으나 해당 AOP 적용시 리턴값을 정상적으로 반환하지 않는
이슈가 있어 사용하지 않게 되었습니다.
 */

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedissonLockAspect {

    private final RedissonClient redissonClient;
    ColumnResponseDto proceed;
    @Around("@annotation(com.sparta.sensorypeople.common.redisson.RedissonLock)")
    public ColumnResponseDto redissonLock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedissonLock annotation = method.getAnnotation(RedissonLock.class);
        String lockKey = annotation.value();

        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean available = lock.tryLock(annotation.waitTime(), annotation.leaseTime(), TimeUnit.MILLISECONDS);
            if (available) {
                try {
                    log.info("로직 수행");
                    proceed = (ColumnResponseDto) joinPoint.proceed();
                } finally {
                    log.info("락 해제");
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            log.info("에러 발생");
            Thread.currentThread().interrupt();

        }

        return proceed;

    }
}