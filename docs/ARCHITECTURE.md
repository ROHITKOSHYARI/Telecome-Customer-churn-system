# Architecture

## Overview

Customer Churn Prediction is implemented as a two-service architecture:

- A Spring Boot backend handles authentication, user management, persistence, API documentation, and orchestration.
- A FastAPI model service handles machine learning inference.

This separation keeps business API concerns independent from model-serving concerns.

## High-Level Flow

```text
Client
  |
  | Bearer JWT
  v
Spring Boot API :8080
  |
  | RestTemplate POST /predict
  v
FastAPI ML Service :8000
  |
  | joblib model inference
  v
scikit-learn Pipeline
```

## Spring Boot Backend

Path:

```text
customerChurn/
```

Main responsibilities:

- Exposes REST endpoints for registration, user profile access, user deletion, and prediction.
- Secures `/user/**` and `/customer/**` endpoints using Spring Security.
- Stores users and customers in PostgreSQL through Spring Data JPA.
- Uses BCrypt to store encoded passwords.
- Calls the FastAPI service through `RestTemplate`.
- Provides Swagger/OpenAPI documentation through `springdoc-openapi`.

Important packages:

| Package | Purpose |
| --- | --- |
| `config` | Security filter chain, password encoder, RestTemplate bean |
| `controller` | REST endpoint definitions |
| `dto` | Request and response data transfer objects |
| `entity` | JPA database models |
| `repository` | Spring Data JPA repositories |
| `service` | Business logic and ML service integration |

## FastAPI ML Service

Path:

```text
CustomerChurnModel/
```

Main responsibilities:

- Loads `Models/churn_model.joblib` at application startup.
- Accepts customer feature data.
- Converts the request into a pandas DataFrame.
- Calls `model.predict` and `model.predict_proba`.
- Returns the predicted churn class and churn probability.

## Database Design

The application uses PostgreSQL with Hibernate-managed tables.

### users

Backed by `User.java`.

Key fields:

- `id`
- `username`
- `password`
- `email`
- `roles`

### customers

Backed by `Customer.java`.

Key fields:

- Demographic fields: `gender`, `seniorCitizen`, `partner`, `dependents`
- Account fields: `tenure`, `contract`, `paymentMethod`, `paperlessBilling`
- Service fields: `phoneService`, `internetService`, `onlineSecurity`, `techSupport`, and related add-ons
- Billing fields: `monthlyCharges`, `totalCharges`
- Prediction fields: `churn`, `churnProbability`

## Security Design

Security configuration is defined in:

```text
customerChurn/src/main/java/com/customerChurn/config/SpringSecurity.java
```

Rules:

- `/public/**` is open.
- Swagger/OpenAPI endpoints are open.
- `/user/**` requires authentication.
- `/customer/**` requires authentication.
- CSRF is disabled for API usage.
- JWT bearer-token authentication is enabled for protected endpoints.

## Integration Point

The backend calls the ML service from:

```text
customerChurn/src/main/java/com/customerChurn/service/PredictionService.java
```

Current ML service URL:

```text
http://localhost:8000/predict
```

The Java DTO uses `@JsonProperty` annotations to map Java-style field names to the feature names expected by the Python model.

## Deployment Notes

The Spring Boot service includes a Dockerfile:

```text
customerChurn/Dockerfile
```

It expects a prebuilt JAR in `target/` and runs it with Java 21.

For full deployment, run these services together:

- PostgreSQL database
- FastAPI ML service
- Spring Boot API

In a containerized environment, replace `localhost` service URLs with Docker network service names.
