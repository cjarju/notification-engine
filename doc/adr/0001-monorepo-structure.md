# ADR 0001: Monorepo Structure

## Status
Accepted

## Context
The project consists of application services and supporting infrastructure that are developed and deployed together.

## Decision
Adopt a **monorepo** structure to manage both application and infrastructure code in a single repository.

## Consequences
- **Pros:** Easier tracking of service dependencies and centralized CI/CD configurations.
- **Cons:** Repository size will grow over time.
