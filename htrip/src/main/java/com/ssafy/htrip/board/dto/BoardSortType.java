// com.ssafy.htrip.board.dto.BoardSortType.java
package com.ssafy.htrip.board.dto;

public enum BoardSortType {
    LATEST("writeDate", "최신순"),
    VIEWS("views", "조회순"),
    LIKES("likes", "추천순");

    private final String fieldName;
    private final String description;

    BoardSortType(String fieldName, String description) {
        this.fieldName = fieldName;
        this.description = description;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDescription() {
        return description;
    }
}