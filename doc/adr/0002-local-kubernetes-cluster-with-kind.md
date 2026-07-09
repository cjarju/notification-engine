# ADR 0002: Local Kubernetes Cluster with Kind

## Status
Accepted

## Context
A local development environment is needed that closely mirrors the production Kubernetes deployment without incurring cloud costs.

## Decision
Use **Kind** (Kubernetes in Docker) to orchestrate deployments locally instead of relying solely on Docker Compose.

## Consequences
- **Pros:** Enables early validation of Kubernetes manifests and deployment configurations.
- **Cons:** Higher initial setup complexity and greater local RAM/CPU usage than Docker Compose.
