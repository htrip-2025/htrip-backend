// src/main/java/com/example/demo/repository/CommentRepository.java
package com.ssafy.htrip.comment.repository;

import com.ssafy.htrip.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoardBoardId(Long boardId);
}
