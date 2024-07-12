package com.sparta.sensorypeople.domain.board.entity;

import com.sparta.sensorypeople.common.TimeStamp;
import com.sparta.sensorypeople.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Table(name="board_member")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardMember extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name="board_id", nullable = false)
    private Board board;


    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 20)
    private String role;




}
