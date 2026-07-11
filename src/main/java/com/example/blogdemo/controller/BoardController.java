package com.example.blogdemo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blogdemo.common.ApiResponse;
import com.example.blogdemo.dto.BoardResponse;
import com.example.blogdemo.service.BoardService;
import com.example.blogdemo.service.BoardService.BoardRequest;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public ApiResponse<List<BoardResponse>> listBoards() {
        return ApiResponse.success(boardService.listBoards());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ApiResponse<BoardResponse> createBoard(@RequestBody BoardRequest request) {
        return ApiResponse.success(boardService.createBoard(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ApiResponse<BoardResponse> updateBoard(@PathVariable Long id, @RequestBody BoardRequest request) {
        return ApiResponse.success(boardService.updateBoard(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResponse<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ApiResponse.success(null);
    }
}
