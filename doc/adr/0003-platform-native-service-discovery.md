# ADR 0003: Platform-Native Service Discovery and Load Balancing

## Status
Accepted

## Context
Early design drafts considered using Spring Cloud Netflix Eureka for service registry and discovery, paired with Spring Cloud LoadBalancer for client-side load balancing between services.

## Decision
1. Reject application-level service registries and client-side load balancers (for example, Eureka and Spring Cloud LoadBalancer).
2. Delegate service discovery, internal network routing, and load balancing entirely to Kubernetes using CoreDNS and K8s Services (and Docker Compose networking for local development).

## Consequences
- **Pros:** Removes application-level runtime dependencies and reduces resource usage by eliminating dedicated registry and client-side load-balancing components.
- **Cons:** Limited to Kubernetes' built-in service discovery and load-balancing capabilities; advanced application-aware routing (for example, weighted routing, latency-aware routing, or custom endpoint selection) requires additional infrastructure or custom client logic.
