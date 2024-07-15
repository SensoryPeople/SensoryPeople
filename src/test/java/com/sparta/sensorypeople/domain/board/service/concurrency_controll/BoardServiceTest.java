//package com.sparta.sensorypeople.domain.board.service;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.orm.ObjectOptimisticLockingFailureException;
//
//import java.util.stream.IntStream;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//class BoardServiceTest {
//    @Autowired
//    private BoardService boardService;
//
//    @BeforeEach
//    void setUp () {
//
//    }
//
//    @Test
//    @DisplayName("낙관적 락을 이용한 보드 업데이트 동시성 제어")
//    void test() {
//
//
////        IntStream.range(0, 100).parallel().forEach(i -> {
////            try {
////                boardService.CCupdateBoard();
////            } catch (ObjectOptimisticLockingFailureException e) {
////                optimisticLockFailures.incrementAndGet();
////            }
////        });
////    }
//
//}