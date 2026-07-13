package com.example.blogdemo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.blogdemo.entity.Board;
import com.example.blogdemo.entity.Role;
import com.example.blogdemo.entity.User;
import com.example.blogdemo.repository.BoardRepository;
import com.example.blogdemo.repository.PostRepository;
import com.example.blogdemo.repository.ReplyRepository;
import com.example.blogdemo.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ForumIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        replyRepository.deleteAll();
        postRepository.deleteAll();
        boardRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void healthEndpointReturnsOk() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void registerLoginAndFetchProfile() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"alice","password":"password123","email":"alice@example.com"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("alice"));

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":" alice ","password":"password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .path("data")
                .path("token")
                .asText();

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("alice"));
    }

    @Test
    void duplicateEmailIsRejected() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"alice","password":"password123","email":"alice@example.com"}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"bob","password":"password123","email":"alice@example.com"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("email already exists"));
    }

    @Test
    void adminCanCreateBoardAndDeleteWhenEmpty() throws Exception {
        String token = adminToken();

        mockMvc.perform(post("/api/boards")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Announcements","description":"Site news","sortOrder":1}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Announcements"));

        MvcResult listResult = mockMvc.perform(get("/api/boards"))
                .andExpect(status().isOk())
                .andReturn();
        long boardId = objectMapper.readTree(listResult.getResponse().getContentAsString())
                .path("data")
                .get(0)
                .path("id")
                .asLong();

        mockMvc.perform(delete("/api/boards/" + boardId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void cannotDeleteBoardWithPosts() throws Exception {
        User admin = createAdmin();
        Board board = new Board();
        board.setName("General");
        board.setDescription("General discussion");
        board.setSortOrder(1);
        board = boardRepository.save(board);

        String token = loginToken(admin.getUsername(), "admin123");

        mockMvc.perform(post("/api/posts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"boardId":%d,"title":"Hello","content":"World"}
                                """.formatted(board.getId())))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/boards/" + board.getId()).header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Cannot delete board with existing posts"));
    }

    @Test
    void unknownBoardIdReturnsNotFoundWhenListingPosts() throws Exception {
        mockMvc.perform(get("/api/posts").param("boardId", "99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Board not found: 99999"));
    }

    @Test
    void boardRenameRejectsDuplicateName() throws Exception {
        String token = adminToken();

        Board first = new Board();
        first.setName("Alpha");
        first.setDescription("First");
        first.setSortOrder(1);
        first = boardRepository.save(first);

        Board second = new Board();
        second.setName("Beta");
        second.setDescription("Second");
        second.setSortOrder(2);
        second = boardRepository.save(second);

        mockMvc.perform(put("/api/boards/" + second.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Alpha","description":"Second","sortOrder":2}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("board name already exists"));
    }

    private String adminToken() throws Exception {
        createAdmin();
        return loginToken("admin", "admin123");
    }

    private User createAdmin() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@example.com");
        admin.setRole(Role.SUPER_ADMIN);
        return userRepository.save(admin);
    }

    private String loginToken(String username, String password) throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode data = objectMapper.readTree(loginResult.getResponse().getContentAsString()).path("data");
        return data.path("token").asText();
    }
}
