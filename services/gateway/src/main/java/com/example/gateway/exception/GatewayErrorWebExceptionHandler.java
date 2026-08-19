package com.example.gateway.exception;

import com.example.gateway.constants.HeaderConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.support.TimeoutException;

import reactor.core.publisher.Mono;

import tools.jackson.databind.json.JsonMapper;

import io.netty.handler.timeout.ReadTimeoutException;

import java.net.ConnectException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/*
 * Customize the error response body to be consistent with the error contract
 */

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger log =
        LoggerFactory.getLogger(GatewayErrorWebExceptionHandler.class);

    private final JsonMapper jsonMapper;

    public GatewayErrorWebExceptionHandler(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable exception) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(exception);
        }

        GatewayError gatewayError = classify(exception);

        /*
         * Only handle failures that represent communication problems with an
         * upstream service. Everything else continues through the normal
         * WebFlux error handling chain.
         */
        if (gatewayError == null) {
            return Mono.error(exception);
        }

        String correlationId =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst(HeaderConstants.CORRELATION_ID);

        String path = exchange.getRequest().getPath().value();

        log.error(
                "Gateway upstream failure: method={} path={} status={} " +
                "correlationId={} exception={}",
                exchange.getRequest().getMethod(),
                path,
                gatewayError.status().value(),
                correlationId,
                exception.toString(),
                exception
        );

        ProblemDetail problemDetail = ProblemDetail.forStatus(gatewayError.status());
        problemDetail.setType(URI.create(gatewayError.type()));
        problemDetail.setTitle(gatewayError.title());
        problemDetail.setDetail(gatewayError.detail());
        problemDetail.setInstance(URI.create(path));
        problemDetail.setProperty("timestamp", OffsetDateTime.now(ZoneOffset.UTC));
        problemDetail.setProperty("correlationId", correlationId);

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(gatewayError.status());
        response.getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        try {
            byte[] body = jsonMapper.writeValueAsBytes(problemDetail);

            response.getHeaders().setContentLength(body.length);

            return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body))
            );

        } catch (Exception serializationException) {
            log.error(
                "Failed to serialize Gateway error response " +
                "for correlationId={}",
                correlationId,
                serializationException
            );

            return Mono.error(serializationException);
        }
    }

    private GatewayError classify(Throwable exception) {

        if (containsCause(exception, ConnectException.class)) {
            return new GatewayError(
                HttpStatus.BAD_GATEWAY,
                "errors/bad-gateway",
                "Bad Gateway",
                "The gateway could not communicate with the upstream service."
            );
        }

        if (containsCause(exception, ReadTimeoutException.class)
                || containsCause(exception, TimeoutException.class)) {
            return new GatewayError(
                HttpStatus.GATEWAY_TIMEOUT,
                "errors/gateway-timeout",
                "Gateway Timeout",
                "The upstream service did not respond within the allowed time."
            );
        }

        return null;
    }

    private boolean containsCause(Throwable exception, Class<? extends Throwable> type) {
        Throwable current = exception;

        while (current != null) {
            if (type.isInstance(current)) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }
}
