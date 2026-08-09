package com.example.ingestion;

import com.example.ingestion.dto.IngestRequest;
import com.example.ingestion.dto.IngestResponse;
import com.example.ingestion.exception.DispatcherCommunicationException;
import com.example.ingestion.constants.ApiPaths;
import com.example.ingestion.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(IngestionController.class)
@Import(GlobalExceptionHandler.class)
class IngestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IngestionService ingestionService;

    @Test
    void ingest_whenPayloadIsInvalid_returns400BadRequest() throws Exception {
        String invalidJson = """
            {
              "userId": null,
              "content": ""
            }
            """;

        mockMvc.perform(post(ApiPaths.NOTIFICATIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.detail").value("One or more fields are invalid"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[?(@.field == 'userId')]").isNotEmpty())
                .andExpect(jsonPath("$.errors[?(@.field == 'category')]").isNotEmpty())
                .andExpect(jsonPath("$.errors[?(@.field == 'channel')]").isNotEmpty())
                .andExpect(jsonPath("$.errors[?(@.field == 'content')]").isNotEmpty());

        verifyNoInteractions(ingestionService);
    }

    @Test
    void ingest_whenDispatcherFails_returns502BadGateway() throws Exception {
        String validJson = """
            {
              "userId": 1001,
              "category": "ACCOUNT",
              "channel": "EMAIL",
              "content": "Test notification"
            }
            """;

        when(ingestionService.processIngestion(any(IngestRequest.class)))
                .thenThrow(new DispatcherCommunicationException(
                        "Dispatcher offline",
                        new RuntimeException()
                ));

        mockMvc.perform(post(ApiPaths.NOTIFICATIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.title") .value("Dispatcher communication error"))
                .andExpect(jsonPath("$.detail") .value("Dispatcher offline"));
    }

    @Test
    void ingest_whenValidPayload_returns200Ok() throws Exception {
        String validJson = """
            {
              "userId": 1001,
              "category": "ACCOUNT",
              "channel": "EMAIL",
              "content": "Test notification"
            }
            """;

        IngestResponse response = new IngestResponse(
                "123-abc",
                1001L,
                "ACCEPTED",
                OffsetDateTime.now()
        );

        when(ingestionService.processIngestion(any(IngestRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post(ApiPaths.NOTIFICATIONS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingId").value("123-abc"))
                .andExpect(jsonPath("$.status").value("ACCEPTED"))
                .andExpect(jsonPath("$.userId").value(1001));
    }
}
