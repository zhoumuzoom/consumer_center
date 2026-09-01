# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository instructions

Follow `AGENTS.md`; it is the repository's authoritative development guide. Important constraints include:

- Never perform database/table deletion operations or delete Git branches.
- Backend code uses Java 17, Spring Boot 3.5.16, MyBatis-Plus 3.5.17, MySQL, Redis, Sa-Token, Nacos, Knife4j, and XXL-Job.
- Business code belongs under `com.iwhalecloud.consumercenter.mvc`, using controller → service → mapper layering. Controllers accept DTOs and return VOs wrapped with `Result` static factory methods; they must not expose entities.
- Throw `BusinessException` for business failures and let `GlobalExceptionHandler` produce responses.
- Service interfaces do not extend MyBatis-Plus `IService`; mapper interfaces extend `BaseMapper`. Prefer `BaseMapper` single-table operations, using XML SQL only when necessary.
- Use field injection with `@Autowired`, `java.time` types, explicit imports, Swagger/OpenAPI annotations, and the existing conversion classes under `mvc/pojo/convert`.
- Do not hand-create generated entities or mappers. Update the `TABLES` array and run `MyBatisCommentGenerator.main`.

## Commands

### Backend (repository root)

```bash
# Run the Spring Boot application (the default profile is dev)
./mvnw spring-boot:run

# Compile/package
./mvnw clean package

# Run all tests
./mvnw test

# Run one test class
./mvnw -Dtest=CustomerServiceImplTest test

# Run one test method
./mvnw -Dtest=CustomerServiceImplTest#methodName test
```

No backend lint or formatter plugin is configured in `pom.xml`.

### Frontend (`frontend/`)

```bash
npm install
npm run dev
npm run build
npm run lint
npm run format
```

The frontend has no test script configured. It is a React 19 application built with Umi Max 4, Ant Design 6, ProComponents, React Query, Axios, G6, Tailwind CSS, and Biome. Despite the stale `AGENTS.md` reference to Vue, the checked-in frontend is React.

## Architecture

This repository contains a Spring Boot API and a separate frontend application.

### Backend

- `CustomerCenterApplication` is the application entry point.
- `mvc/controller` exposes REST APIs for authentication, customers and overview dashboards, customer profiles, customer and industry trees, decision makers/chains, and group reports.
- `mvc/service` and `mvc/service/impl` contain business orchestration. Services generally combine mapper calls, convert persistence entities through `mvc/pojo/convert`, and return DTO/VO models.
- `mvc/mapper` contains MyBatis-Plus `BaseMapper` data access interfaces; `mvc/pojo/entity` maps database tables, while `dto` and `vo` define API boundaries.
- `common/result` defines uniform `Result` and `PageResult` responses. `common/error` and `common/constant/ErrorMessageEnum` define centralized business error handling.
- `AuthInterceptor` protects API requests through Sa-Token. Authentication supports local credentials and optional OAuth2 token introspection; access/refresh sessions are managed separately and persisted through Redis-backed Sa-Token state.
- `ApiLogAspect` records API operations, while `SqlCostInterceptor` observes SQL execution time.
- `config` wires MVC interception, MyBatis-Plus pagination, Redis serialization, and Knife4j/OpenAPI.
- `application.yaml` activates `dev`, builds the datasource from `mysql.*`, configures logical deletion, Sa-Token, Swagger/Knife4j, and optional SSO. `application-dev.yaml` supplies local defaults and imports optional Nacos configuration. Treat its checked-in connection values as environment-specific; do not copy them into new code or documentation.
- Tests include Spring context coverage plus controller tests and service implementation unit tests under matching package paths.

### Frontend

- `frontend/src/app.jsx` provides application runtime setup; `layouts/BasicLayout.jsx` is the main authenticated shell.
- `pages/` contains route-level screens grouped by the same business domains as the backend.
- `api/` contains domain-specific backend clients. Shared Axios behavior is in `utils/request.js`, token persistence is in `utils/token.js`, and authentication state is exposed through `hooks/useAuth.js`.
- `components/G6TreeCanvas.jsx` is the shared graph/tree visualization component. Locale resources live under `locales/`.
- Umi configuration is under `frontend/config/`. Do not treat generated `frontend/src/.umi/` or installed `frontend/node_modules/` content as source code to edit.

The usual end-to-end flow is: page → domain API module → Axios request/token handling → Spring controller → service → mapper/MySQL → converter/VO → uniform `Result` response.
