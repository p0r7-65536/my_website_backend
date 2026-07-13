package com.example.blogdemo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.blogdemo.dto.PostResponse;
import com.example.blogdemo.entity.Board;
import com.example.blogdemo.entity.Post;
import com.example.blogdemo.entity.User;
import com.example.blogdemo.repository.BoardRepository;
import com.example.blogdemo.repository.PostRepository;
import com.example.blogdemo.repository.ReplyRepository;
import com.example.blogdemo.security.SecurityUtils;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;

    public PostService(PostRepository postRepository, BoardRepository boardRepository, ReplyRepository replyRepository) {
        this.postRepository = postRepository;
        this.boardRepository = boardRepository;
        this.replyRepository = replyRepository;
    }

    public Page<PostResponse> listPosts(Long boardId, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        if (boardId != null && !boardRepository.existsById(boardId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found: " + boardId);
        }
        Page<Post> posts = boardId == null
                ? postRepository.findAll(pageable)
                : postRepository.findByBoardId(boardId, pageable);
        return posts.map(PostResponse::from);
    }

    @Transactional
    public PostResponse getPost(Long id) {
        Post post = findPost(id);
        post.setViewCount(post.getViewCount() + 1);
        return PostResponse.from(post);
    }

    public PostResponse createPost(PostRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request body is required");
        }
        if (request.boardId() == null) {
            throw new IllegalArgumentException("boardId is required");
        }
        requireText(request.title(), "title is required");
        requireText(request.content(), "content is required");
        Board board = boardRepository.findById(request.boardId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found: " + request.boardId()));

        Post post = new Post();
        post.setBoard(board);
        post.setUser(SecurityUtils.currentUser());
        post.setTitle(request.title().trim());
        post.setContent(request.content().trim());
        post.setViewCount(0L);
        return PostResponse.from(postRepository.save(post));
    }

    public PostResponse updatePost(Long id, PostUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request body is required");
        }
        Post post = findPost(id);
        ensureOwnerOrAdmin(post.getUser());
        requireText(request.title(), "title is required");
        requireText(request.content(), "content is required");
        post.setTitle(request.title().trim());
        post.setContent(request.content().trim());
        return PostResponse.from(postRepository.save(post));
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = findPost(id);
        ensureOwnerOrAdmin(post.getUser());
        replyRepository.deleteByPostId(id);
        postRepository.delete(post);
    }

    private Post findPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found: " + id));
    }

    private void ensureOwnerOrAdmin(User owner) {
        User currentUser = SecurityUtils.currentUser();
        if (!owner.getId().equals(currentUser.getId()) && !SecurityUtils.hasAdminPower(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    public record PostRequest(Long boardId, String title, String content) {
    }

    public record PostUpdateRequest(String title, String content) {
    }
}
