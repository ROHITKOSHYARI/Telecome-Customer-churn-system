# Setup Guide

This guide explains how to run the complete Customer Churn Prediction project locally.

## Runtime Components

The application has two runtime services:

| Service | Path | Port | Purpose |
| --- | --- | --- | --- |
| Spring Boot API | `customerChurn/` | `8080` | User APIs, security, PostgreSQL persistence, prediction routing |
| FastAPI ML Service | `CustomerChurnModel/` | `8000` | Loads the trained model and returns churn predictions |

PostgreSQL must also be running locally.

## Backend Configuration

The backend configuration is stored in:

```text
customerChurn/src/main/resources/application.yaml
```

Current database settings:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/customer_churn
    username: postgres
    password: root
```

Create the database before starting the backend:

```sql
CREATE DATABASE customer_churn;
```

Hibernate is configured with:

```yaml
ddl-auto: update
```

This lets Spring create or update the required tables automatically during local development.

## Python ML Service Setup

From the project root:

```bash
cd CustomerChurnModel
python -m venv .venv
.venv\Scripts\activate
pip install fastapi uvicorn pandas scikit-learn joblib pydantic
uvicorn app:app --host 0.0.0.0 --port 8000
```

The ML service loads:

```text
CustomerChurnModel/Models/churn_model.joblib
```

If this file is missing, open and run `CustomerChurnModel/Model_train.ipynb` to regenerate the model artifact.

Verify the service:

```bash
curl http://localhost:8000/health
```

Expected response:

```json
{
  "status": "UP"
}
```

## Spring Boot API Setup

From the project root:

```bash
cd customerChurn
.\mvnw.cmd spring-boot:run
```

The API starts at:

```text
http://localhost:8080
```

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

## Required Startup Order

1. Start PostgreSQL.
2. Start the FastAPI ML service on port `8000`.
3. Start the Spring Boot backend on port `8080`.
4. Register a user with `/public/saveuser`.
5. Login with `/public/login` and call secured endpoints using the returned Bearer JWT.

## Common Issues

### Backend Cannot Connect to Database

Check that PostgreSQL is running and that the database credentials in `application.yaml` match your local PostgreSQL user.

### Prediction Endpoint Returns 502

The Spring Boot service calls:

```text
http://localhost:8000/predict
```

Start the FastAPI service before calling `/customer/getpredection`.

### Model File Not Found

The FastAPI service expects:

```text
Models/churn_model.joblib
```

Run the training notebook from inside the `CustomerChurnModel` directory so the relative path is created correctly.
