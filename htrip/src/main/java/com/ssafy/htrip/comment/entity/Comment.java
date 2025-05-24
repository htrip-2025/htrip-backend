package com.ssafy.htrip.comment.entity;

import com.ssafy.htrip.board.entity.Board;
import com.ssafy.htrip.common.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_no", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "write_date", nullable = false, updatable = false)
    private LocalDateTime writeDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(nullable = false)
    private Integer likes = 0;

    @PrePersist
    public void prePersist() {
        writeDate = LocalDateTime.now();
        updateDate = writeDate;
    }

    @PreUpdate
    public void preUpdate() {
        updateDate = LocalDateTime.now();
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