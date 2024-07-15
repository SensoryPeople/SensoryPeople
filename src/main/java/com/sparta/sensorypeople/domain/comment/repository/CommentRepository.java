package com.sparta.sensorypeople.domain.comment.repository;

import com.sparta.sensorypeople.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
