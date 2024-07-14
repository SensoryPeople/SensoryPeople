package com.sparta.sensorypeople.domain.user.repository;

import com.sparta.sensorypeople.domain.user.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * 사용자 리포지토리 인터페이스
 * User 엔티티에 대한 데이터베이스 작업을 처리
 */

public interface UserRepository extends JpaRepository<User, Long> {


    /*
    회원가입 하는 동안 다른 트랜잭션이 들어와 race condition이 발생하는 것을 방지하기 위해 X-Lock 사용
    트랜잭션 1이 select -> commit 하는 사이에 트랜잭션 2가 select를 하면 중복값 검증이 제대로 이루어지지 않을 수 있음.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.loginId = :loginId")
    Optional<User> findByLoginIdForSignup(String loginId);

    /*
    이하 충돌할 일이 없을 것으로 생각되어 동시성 제어 구현하지 않음.
     */
    Optional<User> findByLoginId(String loginId);

    Optional<User> findByUsername(String userName);

}
