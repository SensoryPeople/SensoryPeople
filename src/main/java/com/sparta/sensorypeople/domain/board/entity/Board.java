package com.sparta.sensorypeople.domain.board.entity;

import com.sparta.sensorypeople.common.TimeStamp;
import com.sparta.sensorypeople.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Table(name="boards")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Board extends TimeStamp {

    //기본키
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자
    @ManyToOne
    @JoinColumn(name="userId",nullable = false)
    private User user;

    //보드 이름
    @Column(nullable = false, length = 255)
    private String name;

    //보드 설명
    @Column(nullable = false, length = 255)
    private String description;


}
