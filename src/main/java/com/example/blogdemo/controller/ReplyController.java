package com.example.blogdemo.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.blogdemo.common.ApiResponse;
import com.example.blogdemo.dto.ReplyResponse;
import com.example.blogdemo.service.ReplyService;
import com.example.blogdemo.service.ReplyService.ReplyRequest;

@RestController
@RequestMapping("/api")
public class ReplyController {

    private final ReplyService replyService;

    public ReplyController(ReplyService replyService) {
        this.replyService = replyService;
    }

    @GetMapping("/posts/{id}/replies")
    public ApiResponse<Page<ReplyResponse>> listReplies(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(replyService.listReplies(id, page, size));
    }

    @PostMapping("/posts/{id}/replies")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<ReplyResponse> createReply(@PathVariable Long id, @RequestBody ReplyRequest request) {
        return ApiResponse.success(replyService.createReply(id, request));
    }

    @DeleteMapping("/replies/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> deleteReply(@PathVariable Long id) {
        replyService.deleteReply(id);
        return ApiResponse.success(null);
    }
}
