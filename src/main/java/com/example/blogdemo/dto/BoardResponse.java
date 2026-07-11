package com.example.blogdemo.dto;

import java.time.LocalDateTime;

import com.example.blogdemo.entity.Board;

public record BoardResponse(
        Long id,
        String name,
        String description,
        Integer sortOrder,
        LocalDateTime createdAt) {

    public static BoardResponse from(Board board) {
        return new BoardResponse(
                board.getId(),
                board.getName(),
                board.getDescription(),
                board.getSortOrder(),
                board.getCreatedAt());
    }
}
