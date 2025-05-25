package com.ssafy.htrip.boardlike.entity;

import com.ssafy.htrip.board.entity.Board;
import com.ssafy.htrip.common.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"board_no", "user_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoardLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likesId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_no", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "liked_at", nullable = false, updatable = false)
    private LocalDateTime likedAt;
}