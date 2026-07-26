package com.example.account.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.account.common.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends BaseIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    private User alice;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        alice = userRepository.save(UserTestDataFactory.activeUser("alice"));
        userRepository.saveAll(UserTestDataFactory.activeUsers("bob", "charlie"));
    }

    @Test
    void getUser_returnsHateoasResponse() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", alice.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(alice.getId()))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.users.href").exists());
    }

    @Test
    void getUser_whenUserDoesNotExist_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("User not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void listUsers_returnsPagedResponse() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                .param("active", "true")
                .param("projection", "SUMMARY")
                .param("sort", "id,asc")
                .param("page", "1")
                .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.links.next").exists())
                .andExpect(jsonPath("$.links.prev").exists());
    }

    @Test
    void listUsers_onLastPage_returnsNoNextLink() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                .param("active", "true")
                .param("projection", "SUMMARY")
                .param("sort", "id,asc")
                .param("page", "1")
                .param("size", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page").value(1))
            .andExpect(jsonPath("$.size").value(2))
            .andExpect(jsonPath("$.totalElements").value(3))
            .andExpect(jsonPath("$.totalPages").value(2))
            .andExpect(jsonPath("$.last").value(true))
            .andExpect(jsonPath("$.links.self").exists())
            .andExpect(jsonPath("$.links.prev").exists())
            .andExpect(jsonPath("$.links.next").doesNotExist());
    }

    @Test
    void createUser_withValidRequest_returnsCreatedUser() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "username": "new.user",
                      "email": "new.user@example.com",
                      "phoneNumber": "+12025550102"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("new.user"));
    }

    @Test
    void createUser_withInvalidRequest_returnsValidationErrors() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Validation failed"))
            .andExpect(jsonPath("$.errors").isArray())
            .andExpect(jsonPath("$.errors.length()").value(2));
    }

    @Test
    void patchUser_withValidEmail_returnsUpdatedUser() throws Exception {
        mockMvc.perform(patch("/api/v1/users/{id}", alice.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                    "email": "alice.updated@example.com"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(alice.getId()))
                .andExpect(jsonPath("$.email").value("alice.updated@example.com"));
    }

    @Test
    void patchUser_withInvalidEmail_returnsValidationErrors() throws Exception {
        mockMvc.perform(patch("/api/v1/users/{id}", alice.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                    "email": "not-an-email"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(1));
    }

    @Test
    void deleteUser_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", alice.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_whenUserDoesNotExist_returns404() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }
}
