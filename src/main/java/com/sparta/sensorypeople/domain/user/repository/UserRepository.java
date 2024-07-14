package com.sparta.sensorypeople.domain.user.repository;

import com.sparta.sensorypeople.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자 리포지토리 인터페이스
 * User 엔티티에 대한 데이터베이스 작업을 처리
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByUsername(String username);
}