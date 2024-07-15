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
    이하 충돌할 일이 없을 것으로 생각되어 동시성 제어 구현하지 않음.
     */


    @Query("select u from User u where u.loginId = :loginId")
    Optional<User> findByLoginIdForSignup(String loginId);

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByUsername(String userName);

}
