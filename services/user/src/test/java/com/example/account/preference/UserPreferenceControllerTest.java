package com.example.account.preference;

import com.example.account.common.BaseIntegrationTest;
import com.example.account.preference.enums.AlertCategory;
import com.example.account.preference.enums.DeliveryChannel;
import com.example.account.user.User;
import com.example.account.user.UserRepository;
import com.example.account.user.UserTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

class UserPreferenceControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private UserRepository userRepository;

    private User alice;

    private Long preferenceId;

    @BeforeEach
    void setUp() {
        userPreferenceRepository.deleteAll();
        userRepository.deleteAll();

        alice = userRepository.save(
            UserTestDataFactory.activeUser("alice")
        );

        UserPreference preference =
            userPreferenceRepository.save(
                UserPreferenceTestDataFactory.userPreference(
                    alice,
                    AlertCategory.SECURITY,
                    DeliveryChannel.EMAIL
                )
            );

        preferenceId = preference.getId();
    }

    @Test
    void getPreferences_returns200AndPreferenceList() throws Exception {
        mockMvc.perform(
                get("/api/v1/users/{userId}/preferences", alice.getId())
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].category").value("SECURITY"))
            .andExpect(jsonPath("$[0].channel").value("EMAIL"));
    }

    @Test
    void getPreferences_whenUserDoesNotExist_returns404() throws Exception {
        mockMvc.perform(
                get("/api/v1/users/{userId}/preferences", 99999L)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    void getPreference_returns200AndPreference() throws Exception {
        mockMvc.perform(
                get(
                    "/api/v1/users/{userId}/preferences/{preferenceId}",
                    alice.getId(),
                    preferenceId
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(preferenceId))
            .andExpect(jsonPath("$.category").value("SECURITY"))
            .andExpect(jsonPath("$.channel").value("EMAIL"));
    }

    @Test
    void getPreference_whenPreferenceDoesNotExist_returns404() throws Exception {
        mockMvc.perform(
                get(
                    "/api/v1/users/{userId}/preferences/{preferenceId}",
                    alice.getId(),
                    99999L
                )
            )
            .andExpect(status().isNotFound());
    }

    @Test
    void createPreference_withValidRequest_returns201() throws Exception {
        String request = """
            {
              "category": "SYSTEM",
              "channel": "SMS",
              "enabled": true
            }
            """;

        mockMvc.perform(
                post("/api/v1/users/{userId}/preferences", alice.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.category").value("SYSTEM"))
            .andExpect(jsonPath("$.channel").value("SMS"));
    }

    @Test
    void createPreference_withDuplicatePreference_returns409() throws Exception {
        String request = """
            {
              "category": "SECURITY",
              "channel": "EMAIL",
              "enabled": true
            }
            """;

        mockMvc.perform(
                post("/api/v1/users/{userId}/preferences", alice.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
            )
            .andExpect(status().isConflict());
    }

    @Test
    void patchPreference_withValidRequest_returns200() throws Exception {
        String request = """
            {
              "enabled": false
            }
            """;

        mockMvc.perform(
                patch(
                    "/api/v1/users/{userId}/preferences/{preferenceId}",
                    alice.getId(),
                    preferenceId
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.enabled").value(false));
    }

    @Test
    void patchPreference_whenPreferenceDoesNotExist_returns404() throws Exception {
        String request = """
            {
              "enabled": false
            }
            """;

        mockMvc.perform(
                patch(
                    "/api/v1/users/{userId}/preferences/{preferenceId}",
                    alice.getId(),
                    99999L
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
            )
            .andExpect(status().isNotFound());
    }
}
