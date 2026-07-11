package com.example.blogdemo.dto;

import java.time.LocalDateTime;

import com.example.blogdemo.entity.Reply;

public record ReplyResponse(
        Long id,
        Long postId,
        Long userId,
        String username,
        String content,
        LocalDateTime createdAt) {

    public static ReplyResponse from(Reply reply) {
        return new ReplyResponse(
                reply.getId(),
                reply.getPost().getId(),
                reply.getUser().getId(),
                reply.getUser().getUsername(),
                reply.getContent(),
                reply.getCreatedAt());
    }
}
