package com.ssafy.htrip.board.service;

import com.ssafy.htrip.board.dto.BoardRequestDto;
import com.ssafy.htrip.board.dto.BoardResponseDto;
import com.ssafy.htrip.board.entity.Board;
import com.ssafy.htrip.board.entity.Category;
import com.ssafy.htrip.board.repository.BoardRepository;
import com.ssafy.htrip.board.repository.BoardCategoryRepository;
import com.ssafy.htrip.boardlike.entity.BoardLike;
import com.ssafy.htrip.boardlike.repository.BoardLikeRepository;
import com.ssafy.htrip.common.entity.User;
import com.ssafy.htrip.common.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardCategoryRepository boardCategoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BoardResponseDto createBoard(Integer userId, BoardRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Category category = boardCategoryRepository.findById(dto.getCategoryNo())
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));

        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user)
                .category(category)
                .hasImage(dto.getImageUrls() != null && !dto.getImageUrls().isEmpty())
                .isNotice(dto.getIsNotice() != null && dto.getIsNotice())
                .build();

        Board savedBoard = boardRepository.save(board);

        return mapToDto(savedBoard);
    }

    @Override
    @Transactional
    public BoardResponseDto getBoardDetail(Long boardNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        // 조회수 증가
        board.increaseViews();

        return mapToDto(board);
    }

    @Override
    public Page<BoardResponseDto> getBoards(Pageable pageable) {
        return boardRepository.findAll(pageable)
                .map(this::mapToDto);
    }

    @Override
    public List<BoardResponseDto> getNotices() {
        return boardRepository.findByIsNoticeTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BoardResponseDto> getBoardsByCategory(Integer categoryNo, Pageable pageable) {
        return boardRepository.findByCategoryCategoryNo(categoryNo, pageable)
                .map(this::mapToDto);
    }

    @Override
    public Page<BoardResponseDto> searchBoards(String type, String keyword, Pageable pageable) {
        Page<Board> boards;

        switch (type) {
            case "title":
                boards = boardRepository.findByTitleContaining(keyword, pageable);
                break;
            case "content":
                boards = boardRepository.findByContentContaining(keyword, pageable);
                break;
            case "title_content":
                boards = boardRepository.findByTitleOrContentContaining(keyword, pageable);
                break;
            case "author":
                boards = boardRepository.findByUserNicknameContaining(keyword, pageable);
                break;
            default:
                boards = boardRepository.findAll(pageable);
        }

        return boards.map(this::mapToDto);
    }

    @Override
    public Page<BoardResponseDto> getMyBoards(Integer userId, Pageable pageable) {
        return boardRepository.findByUserUserId(userId, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public BoardResponseDto updateBoard(Integer userId, Long boardNo, BoardRequestDto dto) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        // 작성자 검증
        if (!board.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("게시글 수정 권한이 없습니다.");
        }

        Category category = boardCategoryRepository.findById(dto.getCategoryNo())
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));

        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        board.setCategory(category);
        board.setHasImage(dto.getImageUrls() != null && !dto.getImageUrls().isEmpty());

        if (dto.getIsNotice() != null) {
            board.setIsNotice(dto.getIsNotice());
        }

        return mapToDto(board);
    }

    @Override
    @Transactional
    public void deleteBoard(Integer userId, Long boardNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        // 작성자 검증 (관리자는 별도 처리 필요)
        if (!board.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("게시글 삭제 권한이 없습니다.");
        }

        boardRepository.delete(board);
    }

    @Override
    @Transactional
    public void toggleLike(Integer userId, Long boardNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 이미 좋아요를 눌렀는지 확인
        Optional<BoardLike> existingLike = boardLikeRepository.findByBoardBoardNoAndUserUserId(boardNo, userId);

        if (existingLike.isPresent()) {
            // 이미 좋아요를 눌렀다면 좋아요 취소
            boardLikeRepository.delete(existingLike.get());
            board.decreaseLikes();
        } else {
            // 좋아요를 누르지 않았다면 좋아요 추가
            BoardLike boardLike = BoardLike.builder()
                    .board(board)
                    .user(user)
                    .build();
            boardLikeRepository.save(boardLike);
            board.increaseLikes();
        }
    }

    // Entity -> DTO 변환 메서드 (현재 로그인한 사용자의 좋아요 상태 반영)
    private BoardResponseDto mapToDto(Board board) {
        boolean isLiked = false;
        if (board.getUser().getUserId() != null) {
            isLiked = boardLikeRepository.findByBoardBoardNoAndUserUserId(board.getBoardNo(), board.getUser().getUserId()).isPresent();
        }

        return BoardResponseDto.builder()
                .boardNo(board.getBoardNo())
                .title(board.getTitle())
                .content(board.getContent())
                .userId(board.getUser().getUserId())
                .author(board.getUser().getNickname())
                .profileImgUrl(board.getUser().getProfileImgUrl())
                .categoryNo(board.getCategory().getCategoryNo())
                .category(board.getCategory().getCategoryName())
                .writeDate(board.getWriteDate())
                .updateDate(board.getUpdateDate())
                .views(board.getViews())
                .likes(board.getLikes())
                .commentCount(board.getCommentCount())
                .hasImage(board.getHasImage())
                .isNotice(board.getIsNotice())
//                .isLiked(isLiked)  // 현재 사용자의 좋아요 상태 추가
                .build();
    }

    @Override
    public Page<BoardResponseDto> getLikedBoards(Integer userId, Pageable pageable) {
        List<BoardLike> likes = boardLikeRepository.findByUserUserId(userId);
        List<Long> boardIds = likes.stream()
                .map(like -> like.getBoard().getBoardNo())
                .collect(Collectors.toList());

        if (boardIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<Board> boards = boardRepository.findByBoardIdIn(boardIds, pageable);
        return boards.map(board -> mapToDto(board));
    }
}
