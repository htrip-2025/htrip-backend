// src/main/java/com/example/demo/repository/BoardRepository.java
package com.ssafy.htrip.board.repository;


import com.ssafy.htrip.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
