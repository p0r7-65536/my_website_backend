package com.example.blogdemo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.blogdemo.dto.BoardResponse;
import com.example.blogdemo.entity.Board;
import com.example.blogdemo.repository.BoardRepository;
import com.example.blogdemo.repository.PostRepository;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final PostRepository postRepository;

    public BoardService(BoardRepository boardRepository, PostRepository postRepository) {
        this.boardRepository = boardRepository;
        this.postRepository = postRepository;
    }

    public List<BoardResponse> listBoards() {
        return boardRepository.findAllByOrderBySortOrderAscIdAsc().stream()
                .map(BoardResponse::from)
                .toList();
    }

    public BoardResponse createBoard(BoardRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request body is required");
        }
        requireText(request.name(), "name is required");
        if (boardRepository.existsByName(request.name().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "board name already exists");
        }

        Board board = new Board();
        apply(board, request);
        return BoardResponse.from(boardRepository.save(board));
    }

    public BoardResponse updateBoard(Long id, BoardRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request body is required");
        }
        Board board = findBoard(id);
        requireText(request.name(), "name is required");
        String trimmedName = request.name().trim();
        if (boardRepository.existsByNameAndIdNot(trimmedName, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "board name already exists");
        }
        apply(board, request);
        return BoardResponse.from(boardRepository.save(board));
    }

    public void deleteBoard(Long id) {
        Board board = findBoard(id);
        if (postRepository.countByBoardId(id) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Cannot delete board with existing posts");
        }
        boardRepository.delete(board);
    }

    private Board findBoard(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found: " + id));
    }

    private void apply(Board board, BoardRequest request) {
        board.setName(request.name().trim());
        board.setDescription(request.description());
        board.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    public record BoardRequest(String name, String description, Integer sortOrder) {
    }
}
