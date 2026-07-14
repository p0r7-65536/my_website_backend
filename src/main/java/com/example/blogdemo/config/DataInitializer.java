package com.example.blogdemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.blogdemo.entity.Board;
import com.example.blogdemo.entity.Role;
import com.example.blogdemo.entity.User;
import com.example.blogdemo.repository.BoardRepository;
import com.example.blogdemo.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initForumData(
            UserRepository userRepository,
            BoardRepository boardRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed-data:true}") boolean seedData) {
        return args -> {
            if (!seedData) {
                return;
            }

            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@example.com");
                admin.setRole(Role.SUPER_ADMIN);
                userRepository.save(admin);
            }

            createBoardIfMissing(
                    boardRepository,
                    "Technical Discussion",
                    "Discuss programming, architecture, operations, and engineering practices.",
                    10);
            createBoardIfMissing(
                    boardRepository,
                    "Resource Sharing",
                    "Share tools, tutorials, materials, and project experience.",
                    20);
            createBoardIfMissing(
                    boardRepository,
                    "Casual Chat",
                    "Casual conversation and everyday topics.",
                    30);
        };
    }

    private void createBoardIfMissing(BoardRepository boardRepository, String name, String description, int sortOrder) {
        if (boardRepository.existsByName(name)) {
            return;
        }
        Board board = new Board();
        board.setName(name);
        board.setDescription(description);
        board.setSortOrder(sortOrder);
        boardRepository.save(board);
    }
}
