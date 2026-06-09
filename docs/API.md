# API Documentation

The project exposes APIs from both the Spring Boot backend and the FastAPI ML service.

## Base URLs

| Service | Base URL |
| --- | --- |
| Spring Boot API | `http://localhost:8080` |
| FastAPI ML Service | `http://localhost:8000` |

## Authentication

Spring Boot secures these paths:

```text
/user/**
/customer/**
```

Use HTTP Basic authentication with the username and password created through `/public/saveuser`.

Public paths:

```text
/public/**
/swagger-ui/**
/swagger-ui.html
/v3/api-docs/**
```

## Public API

### Health Check

```http
POST /public/health_check
```

Authentication: not required

Example response:

```text
health check pass
```

### Register User

```http
POST /public/saveuser
```

Authentication: not required

Request body:

```json
{
  "id": 1,
  "username": "demo",
  "password": "demo123",
  "email": "demo@example.com",
  "roles": ["USER"]
}
```

Behavior:

- Encodes the password with BCrypt.
- Saves the user in PostgreSQL.
- Returns `201 Created` on success.

## User API

### Get Authenticated User

```http
GET /user/getuser
```

Authentication: required

Example:

```bash
curl -u demo:demo123 http://localhost:8080/user/getuser
```

Example response:

```json
{
  "id": 1,
  "username": "demo",
  "email": "demo@example.com",
  "roles": ["USER"]
}
```

### Delete Authenticated User

```http
DELETE /user/delete_user
```

Authentication: required

Example:

```bash
curl -X DELETE -u demo:demo123 http://localhost:8080/user/delete_user
```

Returns `204 No Content` on success.

## Prediction API

### Predict Customer Churn Through Spring Boot

```http
POST /customer/getpredection
```

Authentication: required

Note: the endpoint is currently spelled `getpredection` in the controller. Use the exact path unless the code is renamed later.

Example:

```bash
curl -X POST http://localhost:8080/customer/getpredection ^
  -u demo:demo123 ^
  -H "Content-Type: application/json" ^
  -d "{\"gender\":\"Female\",\"seniorCitizen\":0,\"partner\":\"Yes\",\"dependents\":\"No\",\"tenure\":12,\"phoneService\":\"Yes\",\"multipleLines\":\"No\",\"internetService\":\"Fiber optic\",\"onlineSecurity\":\"No\",\"onlineBackup\":\"Yes\",\"deviceProtection\":\"No\",\"techSupport\":\"No\",\"streamingTV\":\"Yes\",\"streamingMovies\":\"Yes\",\"contract\":\"Month-to-month\",\"paperlessBilling\":\"Yes\",\"paymentMethod\":\"Electronic check\",\"monthlyCharges\":89.10,\"totalCharges\":1069.20}"
```

Request fields:

| Field | Type | Example |
| --- | --- | --- |
| `gender` | string | `Female` |
| `seniorCitizen` | integer | `0` |
| `partner` | string | `Yes` |
| `dependents` | string | `No` |
| `tenure` | integer | `12` |
| `phoneService` | string | `Yes` |
| `multipleLines` | string | `No` |
| `internetService` | string | `Fiber optic` |
| `onlineSecurity` | string | `No` |
| `onlineBackup` | string | `Yes` |
| `deviceProtection` | string | `No` |
| `techSupport` | string | `No` |
| `streamingTV` | string | `Yes` |
| `streamingMovies` | string | `Yes` |
| `contract` | string | `Month-to-month` |
| `paperlessBilling` | string | `Yes` |
| `paymentMethod` | string | `Electronic check` |
| `monthlyCharges` | decimal | `89.10` |
| `totalCharges` | decimal | `1069.20` |

Example response:

```json
{
  "churn": "Yes",
  "churnProbability": 0.78
}
```

## FastAPI ML API

### Health Check

```http
GET /health
```

Example response:

```json
{
  "status": "UP"
}
```

### Direct Prediction

```http
POST /predict
```

Authentication: not required

The direct ML endpoint expects feature names matching the training dataset, including capitalized fields such as `SeniorCitizen`, `Partner`, `MonthlyCharges`, and `TotalCharges`.

Example body:

```json
{
  "gender": "Female",
  "SeniorCitizen": 0,
  "Partner": "Yes",
  "Dependents": "No",
  "tenure": 12,
  "PhoneService": "Yes",
  "MultipleLines": "No",
  "InternetService": "Fiber optic",
  "OnlineSecurity": "No",
  "OnlineBackup": "Yes",
  "DeviceProtection": "No",
  "TechSupport": "No",
  "StreamingTV": "Yes",
  "StreamingMovies": "Yes",
  "Contract": "Month-to-month",
  "PaperlessBilling": "Yes",
  "PaymentMethod": "Electronic check",
  "MonthlyCharges": 89.10,
  "TotalCharges": 1069.20
}
```

