package com.example.blogdemo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.example.blogdemo.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Page<Reply> findByPostId(Long postId, Pageable pageable);

    @Modifying
    void deleteByPostId(Long postId);
}
