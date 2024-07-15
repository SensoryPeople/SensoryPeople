package com.sparta.sensorypeople.common.redisson;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {

    String value() default "Lock";// Lock의 이름 (고유값)
    long waitTime() default 5000L; // Lock획득을 시도하는 최대 시간 (ms)
    long leaseTime() default 2000L; // 락을 획득한 후, 점유하는 최대 시간 (ms)

}
