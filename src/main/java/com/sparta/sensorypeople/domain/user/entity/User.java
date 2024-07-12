package com.sparta.sensorypeople.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String loginId;

    @Column(nullable = false, length = 20)
    private String loginPassword;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false)
    private UserAuthEnum userAuth;

    @Column(nullable = false, length = 255, unique = true)
    private String refreshToken;

    public User(String id, String pw, String name, String email, UserAuthEnum auth, String refreshToken){
        this.loginId = id;
        this.loginPassword=pw;
        this.username = name;
        this.email = email;
        this.userAuth = auth;
        this.refreshToken = refreshToken;

    }
}
