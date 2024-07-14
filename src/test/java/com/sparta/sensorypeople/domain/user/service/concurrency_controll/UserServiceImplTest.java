package com.sparta.sensorypeople.domain.user.service.concurrency_controll;

import com.sparta.sensorypeople.domain.column.service.ColumnService;
import com.sparta.sensorypeople.domain.user.dto.SignupRequestDto;
import com.sparta.sensorypeople.domain.user.entity.User;
import com.sparta.sensorypeople.domain.user.entity.UserAuthEnum;
import com.sparta.sensorypeople.domain.user.repository.UserRepository;
import com.sparta.sensorypeople.domain.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ColumnService columnService;

    User user;
    int threadCount = 100;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("회원가입 동시성 제어 테스트")
    @Transactional
    void test() throws InterruptedException {
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                .userId("testuserid1")
                .password("testPassword!")
                .userName("testusername")
                .email("test@test.com")
                .userAuth(UserAuthEnum.USER)
                .adminToken("")
                .build();
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<User>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            futures.add(executorService.submit(() -> {
                                try {
                                    System.out.println(finalI + "번째 thread 접근 시작");
                                    return userServiceImpl.signup(signupRequestDto);
                                } finally {
                                    System.out.println(finalI + "번째 thread 접근 종료");
                                    latch.countDown();
                                }
                            }
                    )
            );
        }

        latch.await();

        long successCount = futures.stream()
                .filter(future -> {
                    try {
                        return future.get() != null;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();



        System.out.println(userRepository.findAll().size());
    }


}