package com.sparta.sensorypeople.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * 사용자 엔티티
 * 회원 정보를 담고 있는 데이터베이스 테이블과 매핑되는 클래스
 */

@Entity
@Table(name="users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30, unique = true)
    private String loginId;

    @Column(nullable = false, length = 255)
    private String loginPassword;

    @Column(nullable = false, length = 50)
    @NotBlank
    private String username;

    @Column(nullable = false, length = 50)
    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserAuthEnum userAuth;

    @Column(nullable = false, length = 255, unique = true)
    private String refreshToken = "";

    public void updateLoginId(String loginId) {
        this.loginId = loginId;
    }

    public void updatePassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public void updateUserName(String username) {
        this.username = username;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateUserAuth(UserAuthEnum userAuth) {
        this.userAuth = userAuth;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
