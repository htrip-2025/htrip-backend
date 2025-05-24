package com.ssafy.htrip.board.entity;

import com.ssafy.htrip.comment.entity.Comment;
import com.ssafy.htrip.common.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_no")
    private Long boardNo;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_no", nullable = false)
    private Category category;

    @Column(name = "write_date", nullable = false, updatable = false)
    private LocalDateTime writeDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(nullable = false)
    private Integer views = 0;

    @Column(nullable = false)
    private Integer likes = 0;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;

    @Column(name = "has_image", nullable = false)
    private Boolean hasImage = false;

    @Column(name = "is_notice", nullable = false)
    private Boolean isNotice = false;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        writeDate = LocalDateTime.now();
        updateDate = writeDate;
    }

    @PreUpdate
    public void preUpdate() {
        updateDate = LocalDateTime.now();
    }

    // 댓글 추가 메서드
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setBoard(this);
        this.commentCount++;
    }

    // 댓글 삭제 메서드
    public void removeComment(Comment comment) {
        comments.remove(comment);
        this.commentCount = Math.max(0, this.commentCount - 1);
    }

    // 조회수 증가 메서드
    public void increaseViews() {
        this.views++;
    }

    // 좋아요 증가/감소 메서드
    public void increaseLikes() {
        this.likes++;
    }

    public void decreaseLikes() {
        if (this.likes > 0) {
            this.likes--;
        }
    }
}