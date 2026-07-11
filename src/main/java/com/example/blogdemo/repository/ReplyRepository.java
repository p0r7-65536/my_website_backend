package com.example.blogdemo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blogdemo.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Page<Reply> findByPostId(Long postId, Pageable pageable);

    void deleteByPostId(Long postId);
}
