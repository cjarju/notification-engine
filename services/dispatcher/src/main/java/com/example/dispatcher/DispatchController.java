package com.example.dispatcher;

import com.example.dispatcher.constants.ApiPaths;
import com.example.dispatcher.dto.DispatchRequest;
import com.example.dispatcher.dto.DispatchResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiPaths.DISPATCH)
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchService dispatchService;

    @PostMapping
    public ResponseEntity<DispatchResponse> dispatch(
        @Valid @RequestBody DispatchRequest request
    ) {
        DispatchResponse response = dispatchService.processDispatch(request);
        return ResponseEntity.ok(response);
    }
}
