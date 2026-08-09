package com.example.ingestion;

import com.example.ingestion.dto.IngestRequest;
import com.example.ingestion.dto.IngestResponse;
import com.example.ingestion.constants.ApiPaths;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiPaths.NOTIFICATIONS)
@RequiredArgsConstructor
public class IngestionController {

    private final IngestionService ingestionService;

    @PostMapping
    public ResponseEntity<IngestResponse> ingestNotification(
        @Valid @RequestBody IngestRequest request
    ) {
        IngestResponse response = ingestionService.processIngestion(request);
        return ResponseEntity.ok(response);
    }
}
