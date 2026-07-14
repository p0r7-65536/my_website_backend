package com.example.blogdemo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.blogdemo.entity.Role;
import com.example.blogdemo.repository.BoardRepository;
import com.example.blogdemo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = "app.seed-data=true")
class ForumSeedDataTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Test
    void seedsSuperAdminAndDefaultBoards() throws Exception {
        assertThat(userRepository.findByUsername("admin")).isPresent();
        assertThat(userRepository.findByUsername("admin").orElseThrow().getRole()).isEqualTo(Role.SUPER_ADMIN);
        assertThat(boardRepository.existsByName("Technical Discussion")).isTrue();
        assertThat(boardRepository.existsByName("Resource Sharing")).isTrue();
        assertThat(boardRepository.existsByName("Casual Chat")).isTrue();

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"admin123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.user.role").value("SUPER_ADMIN"))
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .path("data")
                .path("token")
                .asText();

        mockMvc.perform(get("/api/boards").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Technical Discussion"))
                .andExpect(jsonPath("$.data[1].name").value("Resource Sharing"))
                .andExpect(jsonPath("$.data[2].name").value("Casual Chat"));
    }
}
