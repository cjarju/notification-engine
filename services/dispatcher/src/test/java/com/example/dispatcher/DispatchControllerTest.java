package com.example.dispatcher;

import com.example.dispatcher.constants.ApiPaths;
import com.example.dispatcher.dto.DispatchResponse;
import com.example.dispatcher.enums.DispatchStatus;
import com.example.dispatcher.exception.GlobalExceptionHandler;
import com.example.dispatcher.exception.UserCommunicationException;
import com.example.dispatcher.exception.UserNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DispatchController.class)
@Import(GlobalExceptionHandler.class)
class DispatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DispatchService dispatchService;

    @Test
    void dispatch_whenInvalidPayload_returns400BadRequest() throws Exception {
        String invalidJson = """
            {
            "trackingId": "",
            "userId": null
            }
            """;

        mockMvc.perform(post(ApiPaths.DISPATCH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid"))
                .andExpect(jsonPath("$.errors").isArray());

        verifyNoInteractions(dispatchService);
    }

    @Test
    void dispatch_whenUserNotFound_returns404NotFound() throws Exception {
        String validJson = """
            {
            "trackingId": "track-123",
            "userId": 999,
            "category": "ACCOUNT",
            "channel": "EMAIL",
            "content": "Test content"
            }
            """;

        when(dispatchService.processDispatch(any()))
                .thenThrow(new UserNotFoundException(999L));

        mockMvc.perform(post(ApiPaths.DISPATCH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("User not found"))
                .andExpect(jsonPath("$.detail").value("User not found with id: 999"));
    }

    @Test
    void dispatch_whenUserServiceFails_returns502BadGateway() throws Exception {
        String validJson = """
            {
            "trackingId": "track-123",
            "userId": 1001,
            "category": "ACCOUNT",
            "channel": "EMAIL",
            "content": "Test content"
            }
            """;

        when(dispatchService.processDispatch(any()))
                .thenThrow(new UserCommunicationException("User service down", new RuntimeException()));

        mockMvc.perform(post(ApiPaths.DISPATCH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.title").value("User service communication error"))
                .andExpect(jsonPath("$.detail").value("User service down"));
    }

    @Test
    void dispatch_whenValidPayload_returns200Ok() throws Exception {
        String validJson = """
            {
            "trackingId": "track-123",
            "userId": 1001,
            "category": "ACCOUNT",
            "channel": "EMAIL",
            "content": "Test content"
            }
            """;

        DispatchResponse response = new DispatchResponse(
                "track-123",
                1001L,
                DispatchStatus.DELIVERED,
                "Dispatched",
                OffsetDateTime.now()
        );

        when(dispatchService.processDispatch(any())).thenReturn(response);

        mockMvc.perform(post(ApiPaths.DISPATCH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingId").value("track-123"))
                .andExpect(jsonPath("$.userId").value(1001))
                .andExpect(jsonPath("$.status").value("DELIVERED"))
                .andExpect(jsonPath("$.message").value("Dispatched"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
