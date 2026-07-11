package com.example.blogdemo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.blogdemo.dto.ReplyResponse;
import com.example.blogdemo.entity.Post;
import com.example.blogdemo.entity.Reply;
import com.example.blogdemo.entity.User;
import com.example.blogdemo.repository.PostRepository;
import com.example.blogdemo.repository.ReplyRepository;
import com.example.blogdemo.security.SecurityUtils;

@Service
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;

    public ReplyService(ReplyRepository replyRepository, PostRepository postRepository) {
        this.replyRepository = replyRepository;
        this.postRepository = postRepository;
    }

    public Page<ReplyResponse> listReplies(Long postId, int page, int size) {
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found: " + postId);
        }
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.ASC, "createdAt"));
        return replyRepository.findByPostId(postId, pageable).map(ReplyResponse::from);
    }

    public ReplyResponse createReply(Long postId, ReplyRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request body is required");
        }
        requireText(request.content(), "content is required");
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found: " + postId));

        Reply reply = new Reply();
        reply.setPost(post);
        reply.setUser(SecurityUtils.currentUser());
        reply.setContent(request.content().trim());
        return ReplyResponse.from(replyRepository.save(reply));
    }

    public void deleteReply(Long id) {
        Reply reply = replyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reply not found: " + id));
        ensureOwnerOrAdmin(reply.getUser());
        replyRepository.delete(reply);
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

    public record ReplyRequest(String content) {
    }
}
