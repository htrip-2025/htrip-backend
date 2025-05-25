package com.ssafy.htrip.board.repository;

import com.ssafy.htrip.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 게시글 전체 조회 (페이징 처리)
    Page<Board> findAll(Pageable pageable);

    // 특정 카테고리의 최신 게시글 1개 조회
    Board findFirstByBoardCategoryCategoryNoOrderByWriteDateDesc(Integer categoryNo);

    // 특정 카테고리를 제외한 게시글 조회 (페이징)
    Page<Board> findByBoardCategoryCategoryNoNot(Integer categoryNo, Pageable pageable);

    // 카테고리별 게시글 조회
    Page<Board> findByBoardCategoryCategoryNo(Integer categoryNo, Pageable pageable);

    // 제목 검색
    Page<Board> findByTitleContaining(String keyword, Pageable pageable);

    // 내용 검색
    Page<Board> findByContentContaining(String keyword, Pageable pageable);

    // 제목 + 내용 검색
    @Query("SELECT b FROM Board b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    Page<Board> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);

    // 작성자 검색
    Page<Board> findByUserNicknameContaining(String keyword, Pageable pageable);

    // 특정 사용자가 작성한 게시글 조회
    Page<Board> findByUserUserId(Integer userId, Pageable pageable);

//    @Query("SELECT b FROM Board b WHERE b.boardId IN :boardIds")
//    Page<Board> findById(@Param("boardIds") Collection<Long> boardIds, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.boardNo IN :boardIds")
    Page<Board> findByBoardIdIn(@Param("boardIds") Collection<Long> boardIds, Pageable pageable);


    // 특정 사용자의 게시글 수 조회
    Long countByUserUserId(Integer userId);
}