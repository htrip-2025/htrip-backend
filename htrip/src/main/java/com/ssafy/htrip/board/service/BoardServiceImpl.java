package com.ssafy.htrip.board.service;

import com.ssafy.htrip.board.dto.*;
import com.ssafy.htrip.board.entity.Board;
import com.ssafy.htrip.board.entity.BoardCategory;
import com.ssafy.htrip.board.entity.BoardImage;
import com.ssafy.htrip.board.repository.BoardImageRepository;
import com.ssafy.htrip.board.repository.BoardRepository;
import com.ssafy.htrip.board.repository.BoardCategoryRepository;
import com.ssafy.htrip.boardlike.entity.BoardLike;
import com.ssafy.htrip.boardlike.repository.BoardLikeRepository;
import com.ssafy.htrip.common.entity.Role;
import com.ssafy.htrip.common.entity.User;
import com.ssafy.htrip.common.repository.UserRepository;
import com.ssafy.htrip.common.service.FileUploadService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
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
    private final BoardImageRepository boardImageRepository;
    private final FileUploadService fileUploadService;

    private void checkNoticePermission(User user) {
        if (user == null || !Role.ADMIN.equals(user.getRole())) {
            throw new AccessDeniedException("공지사항은 관리자만 작성할 수 있습니다.");
        }
    }

    @Override
    @Transactional
    public BoardResponseDto createBoard(Integer userId, BoardRequestDto dto, List<MultipartFile> files) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        if (dto.getCategoryNo() != null && dto.getCategoryNo() == 1) {
            checkNoticePermission(user);
        }

        BoardCategory boardCategory = boardCategoryRepository.findById(dto.getCategoryNo())
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));

        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user)
                .boardCategory(boardCategory)
                .hasImage(dto.getImageUrls() != null && !dto.getImageUrls().isEmpty())
                .build();

        Board savedBoard = boardRepository.save(board);
        if (files != null && !files.isEmpty()) {
            addImagesToBoard(savedBoard.getBoardNo(), files);
            savedBoard.setHasImage(true);
        }

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
    public BoardListWithNoticeDto getBoards(BoardSortType sortType, Pageable pageable) {
        // 1. 최신 공지사항 1개 조회
        Board latestNotice = boardRepository.findFirstByBoardCategoryCategoryNoOrderByWriteDateDesc(1);

        // 2. 정렬 기준에 따라 일반 게시글 조회
        Page<Board> regularBoards;

        // 파라미터로 받은 pageable의 페이지 번호와 크기는 유지하고, 정렬 조건만 변경
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, sortType.getFieldName())
        );

        // 카테고리 1(공지사항)을 제외한 게시글 조회
        regularBoards = boardRepository.findByBoardCategoryCategoryNoNot(1, sortedPageable);

        // 3. 결과를 DTO로 변환
        BoardListWithNoticeDto result = new BoardListWithNoticeDto();
        result.setSortType(sortType);

        // 공지사항이 존재하는 경우에만 설정
        if (latestNotice != null) {
            result.setLatestNotice(mapToDto(latestNotice));
        }

        // 일반 게시글 목록 변환
        result.setBoards(regularBoards.map(this::mapToDto));

        return result;
    }

    @Override
    public BoardListWithNoticeDto  getBoardsByCategory(BoardSortType sortType, Integer categoryNo, Pageable pageable) {
        // 1. 최신 공지사항 1개 조회
        Board latestNotice = boardRepository.findFirstByBoardCategoryCategoryNoOrderByWriteDateDesc(1);

        // 2. 정렬 기준에 따라 일반 게시글 조회
        Page<Board> regularBoards;

        // 파라미터로 받은 pageable의 페이지 번호와 크기는 유지하고, 정렬 조건만 변경
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, sortType.getFieldName())
        );

        // 카테고리 1(공지사항)을 제외한 게시글 조회
        regularBoards = boardRepository.findByBoardCategoryCategoryNo(categoryNo, sortedPageable);

        // 3. 결과를 DTO로 변환
        BoardListWithNoticeDto result = new BoardListWithNoticeDto();
        result.setSortType(sortType);

        // 공지사항이 존재하는 경우에만 설정
        if (latestNotice != null) {
            result.setLatestNotice(mapToDto(latestNotice));
        }

        // 일반 게시글 목록 변환
        result.setBoards(regularBoards.map(this::mapToDto));

        return result;
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
    public BoardResponseDto updateBoard(Integer userId, Long boardNo, BoardRequestDto dto, List<MultipartFile> files) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 본인 게시글인지 확인 (관리자는 모든 게시글 수정 가능)
        if (!board.getUser().getUserId().equals(userId) && !Role.ADMIN.equals(user.getRole())) {
            throw new AccessDeniedException("게시글 수정 권한이 없습니다.");
        }

        // 공지사항 카테고리 변경 시 권한 확인
        if (dto.getCategoryNo() != null && dto.getCategoryNo() == 1 &&
                !board.getBoardCategory().getCategoryNo().equals(1)) {
            checkNoticePermission(user);
        }

        BoardCategory boardCategory = boardCategoryRepository.findById(dto.getCategoryNo())
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));

        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        board.setBoardCategory(boardCategory);

        // 파일이 제공된 경우 이미지 처리
        if (files != null && !files.isEmpty()) {
            // 이미지 추가 처리
            addImagesToBoard(board.getBoardNo(), files);
            board.setHasImage(true);
        }

        Board savedBoard = boardRepository.save(board);

        return mapToDto(savedBoard);
    }

    @Override
    @Transactional
    public void deleteBoard(Integer userId, Long boardNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 공지사항 삭제 시 권한 확인
        if (board.getBoardCategory().getCategoryNo() == 1) {
            checkNoticePermission(user);
        } else if (!board.getUser().getUserId().equals(userId) && !Role.ADMIN.equals(user.getRole())) {
            // 일반 게시글은 작성자 또는 관리자만 삭제 가능
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

    // 이미지 추가 메소드 구현
    @Override
    @Transactional
    public List<BoardImageDto> addImagesToBoard(Long boardNo, List<MultipartFile> files) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        List<BoardImage> savedImages = new ArrayList<>();
        int orderNum = 1;

        // 기존 이미지 개수 확인하여 순서 번호 조정
        Long existingImagesCount = boardImageRepository.countByBoardBoardNo(boardNo);
        if (existingImagesCount > 0) {
            orderNum += existingImagesCount.intValue();
        }

        for (MultipartFile file : files) {
            try {
                // 파일 업로드
                String imagePath = fileUploadService.uploadFile(file, "board");

                // 이미지 엔티티 생성 및 저장
                BoardImage image = BoardImage.builder()
                        .board(board)
                        .imagePath(imagePath)
                        .originalFileName(file.getOriginalFilename())
                        .storedFileName(imagePath.substring(imagePath.lastIndexOf("/") + 1))
                        .fileSize(file.getSize())
                        .orderNum(orderNum++)
                        .build();

                savedImages.add(boardImageRepository.save(image));
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 실패", e);
            }
        }

        // board의 hasImage 필드 업데이트
        if (!savedImages.isEmpty() || existingImagesCount > 0) {
            board.setHasImage(true);
            boardRepository.save(board);
        }

        return savedImages.stream()
                .map(this::toImageDto)
                .collect(Collectors.toList());
    }

    // 이미지 조회 메소드 구현
    @Override
    public List<BoardImageDto> getBoardImages(Long boardNo) {
        List<BoardImage> images = boardImageRepository.findByBoardBoardNoOrderByOrderNumAsc(boardNo);
        return images.stream()
                .map(this::toImageDto)
                .collect(Collectors.toList());
    }

    // 이미지 삭제 메소드 구현
    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        BoardImage image = boardImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("이미지를 찾을 수 없습니다."));

        // 파일 삭제
        fileUploadService.deleteFile(image.getImagePath());

        // DB에서 이미지 정보 삭제
        boardImageRepository.delete(image);

        // 게시글의 모든 이미지가 삭제되었는지 확인
        Long remainingImages = boardImageRepository.countByBoardBoardNo(image.getBoard().getBoardNo());
        if (remainingImages == 0) {
            Board board = image.getBoard();
            board.setHasImage(false);
            boardRepository.save(board);
        }
    }

    // BoardImage -> BoardImageDto 변환 메소드
    private BoardImageDto toImageDto(BoardImage image) {
        return BoardImageDto.builder()
                .imageId(image.getImageId())
                .imagePath(image.getImagePath())
                .originalFileName(image.getOriginalFileName())
                .storedFileName(image.getStoredFileName())
                .fileSize(image.getFileSize())
                .orderNum(image.getOrderNum())
                .build();
    }

    // Entity -> DTO 변환 메서드 (현재 로그인한 사용자의 좋아요 상태 반영)
    private BoardResponseDto mapToDto(Board board) {
        boolean isLiked = false;
        if (board.getUser().getUserId() != null) {
            isLiked = boardLikeRepository.findByBoardBoardNoAndUserUserId(board.getBoardNo(), board.getUser().getUserId()).isPresent();
        }
        List<BoardImageDto> images = boardImageRepository.findByBoardBoardNoOrderByOrderNumAsc(board.getBoardNo())
                .stream()
                .map(this::toImageDto)
                .collect(Collectors.toList());

        return BoardResponseDto.builder()
                .boardNo(board.getBoardNo())
                .title(board.getTitle())
                .content(board.getContent())
                .userId(board.getUser().getUserId())
                .author(board.getUser().getNickname())
                .profileImgUrl(board.getUser().getProfileImgUrl())
                .categoryNo(board.getBoardCategory().getCategoryNo())
                .category(board.getBoardCategory().getCategoryName())
                .writeDate(board.getWriteDate())
                .updateDate(board.getUpdateDate())
                .views(board.getViews())
                .likes(board.getLikes())
                .commentCount(board.getCommentCount())
                .hasImage(!images.isEmpty())
                .images(images)
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
