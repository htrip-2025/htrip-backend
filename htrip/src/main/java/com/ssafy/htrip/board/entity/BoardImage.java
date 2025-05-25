package com.ssafy.htrip.board.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_images")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoardImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_no", nullable = false)
    private Board board;

    @Column(nullable = false)
    private String imagePath;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String storedFileName;

    @Column
    private Long fileSize;

    @Column
    private Integer orderNum;  // 이미지 표시 순서
}