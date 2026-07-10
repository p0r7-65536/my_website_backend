package com.example.blogdemo.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.blogdemo.common.ApiResponse;
import com.example.blogdemo.dto.PostResponse;
import com.example.blogdemo.service.PostService;
import com.example.blogdemo.service.PostService.PostRequest;
import com.example.blogdemo.service.PostService.PostUpdateRequest;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ApiResponse<Page<PostResponse>> listPosts(
            @RequestParam(required = false) Long boardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(postService.listPosts(boardId, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<PostResponse> getPost(@PathVariable Long id) {
        return ApiResponse.success(postService.getPost(id));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PostResponse> createPost(@RequestBody PostRequest request) {
        return ApiResponse.success(postService.createPost(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PostResponse> updatePost(@PathVariable Long id, @RequestBody PostUpdateRequest request) {
        return ApiResponse.success(postService.updatePost(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ApiResponse.success(null);
    }
}
