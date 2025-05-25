package com.ssafy.htrip.board.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board_category")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoardCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_no")
    private Integer categoryNo;

    @Column(name = "category_name", nullable = false, length = 20)
    private String categoryName;

    @OneToMany(mappedBy = "boardCategory", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Board> boards = new ArrayList<>();
}