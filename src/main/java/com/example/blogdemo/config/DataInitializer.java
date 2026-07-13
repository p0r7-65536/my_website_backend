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
            @Value("${app.seed-data:false}") boolean seedData) {
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

            createBoardIfMissing(boardRepository, "技术讨论", "交流编程、架构、运维和工程实践。", 10);
            createBoardIfMissing(boardRepository, "资源分享", "分享工具、教程、资料和项目经验。", 20);
            createBoardIfMissing(boardRepository, "闲聊灌水", "轻松交流日常话题。", 30);
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
