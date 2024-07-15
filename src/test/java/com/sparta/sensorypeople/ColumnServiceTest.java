package com.sparta.sensorypeople;

import com.sparta.sensorypeople.common.StatusCommonResponse;
import com.sparta.sensorypeople.domain.board.entity.Board;
import com.sparta.sensorypeople.domain.column.dto.ColumnRequestDto;
import com.sparta.sensorypeople.domain.column.repository.ColumnRepository;
import com.sparta.sensorypeople.domain.column.service.ColumnService;
import com.sparta.sensorypeople.domain.user.entity.User;
import com.sparta.sensorypeople.domain.user.entity.UserAuthEnum;
import com.sparta.sensorypeople.security.service.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ColumnServiceTest {


    @Autowired
    private ColumnService columnService;

    private User user;
    private Board board;
    @Autowired
    private ColumnRepository columnRepository;

    @BeforeEach
    void setUp() {

        user = new User(5l, "testId",
                "testPassword",
                "testusername",
                "test@test.com",
                UserAuthEnum.ADMIN,
                "");
        ;
    }

    @Test
    @DisplayName("컬럼 생성 테스트 : X-LOCK 동시성제어")
    @Transactional
    void test() throws InterruptedException {
        //given
        int threadCount = 100;
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        ColumnRequestDto columnRequestDto = new ColumnRequestDto("testColumnName1");
        Long boardId = 2L;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<StatusCommonResponse>> futures = new ArrayList<>();

        //when

        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            futures.add(executorService.submit(() -> {
                                try {
//                                    System.out.println(finalI + "번째 thread 접근 시작");
                                    return columnService.createColumn(userDetails, columnRequestDto, boardId);
                                } finally {
//                                    System.out.println(finalI + "번째 thread 접근 종료");
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

        //then
        assertEquals(successCount, 1);
    }

    @Test
    @DisplayName("컬럼 생성 테스트 : 분산 LOCK 동시성제어")
    @Transactional
    void test2() throws InterruptedException {
        //given
        int threadCount = 30;
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        ColumnRequestDto columnRequestDto = new ColumnRequestDto("testColumnName1");
        Long boardId = 2L;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<StatusCommonResponse>> futures = new ArrayList<>();

        //when

        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            futures.add(executorService.submit(() -> {
                                try {
//                                    System.out.println(finalI + "번째 thread 접근 시작");
                                    return columnService.redissonCreateColumn2(userDetails, columnRequestDto, boardId);
                                } finally {
//                                    System.out.println(finalI + "번째 thread 접근 종료");
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

        //then
         assertEquals(1, columnRepository.count());
    }
}






//    @Test
//    @DisplayName("컬럼 생성 테스트")
//    @Transactional
//    void test2() throws InterruptedException {
//        //given
//        int threadCount = 5;
//        UserDetailsImpl userDetails = new UserDetailsImpl(user);
//        ColumnRequestDto columnRequestDto = new ColumnRequestDto("testColumnName1");
//        Long boardId = 1L;
//        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//        List<Future<StatusCommonResponse>> futures = new ArrayList<>();
//
//        //when
//
//        for (int i = 0; i < threadCount; i++) {
//            int finalI = i;
//            futures.add(executorService.submit(() -> {
//                try {
//                    System.out.println(finalI + "번째 thread 접근 시작");
//                    return columnService.redissonCreateColumn(userDetails, columnRequestDto, boardId);
//                } finally {
//                    System.out.println(finalI + "번째 thread 접근 종료");
//                    latch.countDown();
//                }
//            }));
//        }
//
//        latch.await();
//
//        long successCount = futures.stream()
//                .filter(future -> {
//                    try {
//                        return future.get() != null;
//                    } catch (Exception e) {
//                        return false;
//                    }
//                })
//                .count();
//
//        //then
//        System.out.println(successCount);
//        System.out.println("=====================");
//        System.out.println("sout " + futures.size());
//        System.out.println("=====================");
//    }
//
//}


//    @Test
//    @DisplayName("컬럼 생성 테스트:동시성제어")
//    @Transactional
//    void test2() {
//        //given
//        UserDetailsImpl userDetails = new UserDetailsImpl(user);
//        ColumnRequestDto columnRequestDto = new ColumnRequestDto("testColumnName");
//        Long boardId = 1L;
//        ExecutorService excutorService = Executors.newFixedThreadPool(1);
//
//
//        //when
//        for(int i =0; i<5; i++){
//            excutorService.execute(() ->columnService.createColumn(userDetails, columnRequestDto, boardId));
//        }
//
//
//        //then
//        System.out.println(columnRepository.count());
//
//    }
