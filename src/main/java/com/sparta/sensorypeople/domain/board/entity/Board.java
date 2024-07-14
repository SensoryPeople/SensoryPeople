package com.sparta.sensorypeople.domain.board.entity;

import com.sparta.sensorypeople.common.TimeStamp;
import com.sparta.sensorypeople.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "boards")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Board extends TimeStamp {

    // 기본키
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    // 보드 이름
    @Column(nullable = false, length = 255)
    private String name;

    // 보드 설명
    @Column(nullable = false, length = 255)
    private String description;

    // 생성자
    public Board(String name, String description, User user) {
        this.name = name;
        this.description = description;
        this.user = user;
    }
}