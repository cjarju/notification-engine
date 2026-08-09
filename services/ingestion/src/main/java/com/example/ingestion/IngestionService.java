package com.example.ingestion;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.ingestion.dto.DispatchRequest;
import com.example.ingestion.dto.DispatchResponse;
import com.example.ingestion.dto.IngestRequest;
import com.example.ingestion.dto.IngestResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionService {

    private final DispatcherClient dispatcherClient;

    public IngestResponse processIngestion(IngestRequest request) {
        String trackingId = UUID.randomUUID().toString();
        log.info("Ingesting notification request for userId: {}, trackingId: {}", request.userId(), trackingId);

        DispatchRequest dispatchRequest = toDispatchRequest(request, trackingId);

        // Synchronous call
        DispatchResponse dispatchResponse = dispatcherClient.dispatchNotification(dispatchRequest);

        return new IngestResponse(
            trackingId,
            request.userId(),
            dispatchResponse.status(),
            OffsetDateTime.now()
        );
    }

    private DispatchRequest toDispatchRequest(IngestRequest request, String trackingId) {
        return new DispatchRequest(
            trackingId,
            request.userId(),
            request.category(),
            request.channel(),
            request.content(),
            request.metadata()
        );
    }
}
