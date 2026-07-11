package com.example.blogdemo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blogdemo.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByOrderBySortOrderAscIdAsc();

    boolean existsByName(String name);
}
