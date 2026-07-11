package com.example.blogdemo.dto;

import java.time.LocalDateTime;

import com.example.blogdemo.entity.Post;

public record PostResponse(
        Long id,
        Long boardId,
        String boardName,
        Long userId,
        String username,
        String title,
        String content,
        Long viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getBoard().getId(),
                post.getBoard().getName(),
                post.getUser().getId(),
                post.getUser().getUsername(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getCreatedAt(),
                post.getUpdatedAt());
    }
}
