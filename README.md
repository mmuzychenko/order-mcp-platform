# order-mcp-platform

Java Order Service exposed to an AI Agent via MCP (requirements v2, deployment model A).

```
User -> order-agent (LLM + MCP client) -> order-mcp-server -> order-service -> DB
```

| Module           | Port | Responsibility |
|------------------|------|----------------|
| order-contracts  | -    | Shared DTOs, error envelope, versioned JSON Schemas |
| order-service    | 8080 | Authoritative: authN/Z, business rules, transactions, persistence |
| order-mcp-server | 8081 | MCP protocol + contract mapping only (Streamable HTTP) |
| order-agent      | 8082 | LLM + MCP client + execution loop + confirmation |

## Getting started
1. `cp .env.example .env` and fill in local values. Never commit `.env`.
2. `mvn clean verify`
3. Run each app with `mvn -pl <module> spring-boot:run`.

## Milestones
- **M1** search_orders end-to-end with authN + Java authorization.
- **M2** create_order with confirmation, idempotency, audit, status recovery.

## Security checks
Run dependency and code scanning in CI (e.g. Snyk or Trivy for dependencies, Semgrep for code).
