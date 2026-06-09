# Customer Churn Prediction Platform

A comprehensive end-to-end customer churn prediction system built with Spring Boot, PostgreSQL, FastAPI, and scikit-learn. This platform predicts whether a telecom customer is likely to churn and provides a churn probability score for customer retention prioritization.

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Setup Instructions](#setup-instructions)
- [API Documentation](#api-documentation)
  - [Swagger/OpenAPI](#swagger-openapi)
  - [Public Endpoints](#public-endpoints)
  - [User Endpoints](#user-endpoints)
  - [Customer Endpoints](#customer-endpoints)
  - [ML Service Endpoints](#ml-service-endpoints)
- [Authentication](#authentication)
- [Database Schema](#database-schema)
- [Running the Application](#running-the-application)
- [Resume Notes](#resume-notes)

---

## Overview

Customer Churn Prediction is a two-service architecture that combines a secured Spring Boot REST API with a dedicated FastAPI machine learning microservice. The system enables:

- User registration and profile management
- Secured API endpoints with HTTP Basic authentication
- Real-time customer churn prediction with probability scoring
- PostgreSQL persistence for users and customer data
- Full Swagger/OpenAPI documentation

---

## Architecture

### High-Level Flow

```
Client
  |
  | HTTP Basic Auth
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
  |
  v
PostgreSQL Database
```

### Components

| Component | Role | Port |
|-----------|------|------|
| **Spring Boot Backend** | API, Security, Database ORM | 8080 |
| **FastAPI ML Service** | Model Inference | 8000 |
| **PostgreSQL** | Data Persistence | 5432 |

### Key Responsibilities

**Spring Boot Backend (`customerChurn/`):**
- REST endpoint definitions (registration, profile, prediction routing)
- Spring Security with HTTP Basic authentication
- Spring Data JPA with PostgreSQL
- BCrypt password encoding
- RestTemplate calls to FastAPI service
- Swagger/OpenAPI documentation via `springdoc-openapi`

**FastAPI ML Service (`CustomerChurnModel/`):**
- Loads `Models/churn_model.joblib` at startup
- Accepts customer feature data
- Converts data to pandas DataFrame
- Calls scikit-learn model inference
- Returns churn prediction and probability

---

## Technology Stack

### Backend
- **Framework:** Spring Boot 3.5.0
- **Language:** Java 21
- **Security:** Spring Security 3.5.0
- **Database ORM:** Spring Data JPA / Hibernate
- **Database:** PostgreSQL
- **Password Encoding:** BCrypt
- **API Documentation:** SpringDoc OpenAPI 2.8.0
- **REST Client:** Spring RestTemplate
- **Build Tool:** Maven 3.x
- **Logging:** SLF4J / Logback
- **Utility:** Lombok

### ML Service
- **Framework:** FastAPI (Python 3.x)
- **Web Server:** Uvicorn
- **ML Framework:** scikit-learn
- **Data Processing:** pandas
- **Model Serialization:** joblib
- **Data Validation:** Pydantic

---

## Prerequisites

### System Requirements
- **Java:** JDK 21 or later
- **Python:** 3.9 or later
- **Maven:** 3.6 or later
- **PostgreSQL:** 12 or later
- **Git:** For version control

### Tools
- VS Code, IntelliJ IDEA, or any Java IDE
- Postman or cURL for API testing
- Python virtual environment (venv or conda)

---

## Project Structure

```
customerChurnpredection/
├── customerChurn/                          # Spring Boot Backend
│   ├── src/main/java/com/customerChurn/
│   │   ├── controller/
│   │   │   ├── Public.java                # Public endpoints (health, registration)
│   │   │   ├── UserController.java        # User profile management
│   │   │   └── CustomerController.java    # Customer churn prediction
│   │   ├── service/
│   │   │   ├── Userservice.java           # User business logic
│   │   │   ├── UserServiceIMPL.java       # User service implementation
│   │   │   ├── PredictionService.java     # ML service integration
│   │   │   └── Customerservice.java       # Customer business logic
│   │   ├── entity/
│   │   │   ├── User.java                  # User JPA entity
│   │   │   └── Customer.java              # Customer JPA entity
│   │   ├── dto/
│   │   │   ├── UserResponse.java          # User response DTO
│   │   │   ├── PredictionRequest.java     # Prediction request DTO
│   │   │   ├── PredectionResponse.java    # Prediction response DTO
│   │   │   └── ChangePasswordRequest.java # Password change DTO
│   │   ├── repository/
│   │   │   ├── UserRepositories.java      # User JPA repository
│   │   │   └── CustomerRepositories.java  # Customer JPA repository
│   │   ├── config/
│   │   │   ├── AppConfig.java             # Application configuration
│   │   │   └── SpringSecurity.java        # Spring Security configuration
│   │   ├── Enum/
│   │   │   └── Churn.java                 # Churn status enum
│   │   └── CustomerChurnApplication.java  # Main application class
│   ├── src/main/resources/
│   │   ├── application.yaml                # Application configuration
│   │   └── logback.xml                     # Logging configuration
│   ├── src/test/java/com/customerChurn/   # Unit and integration tests
│   ├── pom.xml                            # Maven configuration
│   ├── mvnw / mvnw.cmd                    # Maven wrapper scripts
│   ├── Dockerfile                         # Docker image definition
│   └── target/                            # Build output
│
├── CustomerChurnModel/                     # FastAPI ML Service
│   ├── app.py                             # FastAPI application
│   ├── requirements.txt                   # Python dependencies
│   ├── requirements-dev.txt                # Development dependencies
│   ├── Model_train.ipynb                  # Model training notebook
│   ├── Dockerfile                         # Docker image definition
│   ├── Models/
│   │   └── churn_model.joblib             # Trained scikit-learn model
│   ├── Data/
│   │   └── WA_Fn-UseC_-Telco-Customer-Churn.csv # Training dataset
│   ├── tests/
│   │   └── test_app.py                    # ML service tests
│   └── Churn/                             # Python virtual environment
│
├── docs/
│   ├── API.md                             # Detailed API documentation
│   ├── ARCHITECTURE.md                    # Architecture deep dive
│   ├── SETUP.md                           # Setup guide
│   ├── ML_WORKFLOW.md                     # ML model workflow
│   └── RESUME_NOTES.md                    # Portfolio and resume notes
│
└── README.md                               # This file
```

---

## Setup Instructions

### 1. Prerequisites Setup

#### Create PostgreSQL Database

```bash
# Connect to PostgreSQL
psql -U postgres

# Create the database
CREATE DATABASE customer_churn;

# Exit psql
\q
```

#### Update Database Credentials

Edit `customerChurn/src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/customer_churn
    username: postgres
    password: root  # Change to your PostgreSQL password
  jpa:
    hibernate:
      ddl-auto: update
```

### 2. FastAPI ML Service Setup

```bash
# Navigate to ML service directory
cd CustomerChurnModel

# Create Python virtual environment
python -m venv Churn

# Activate virtual environment
# On Windows:
Churn\Scripts\activate
# On macOS/Linux:
source Churn/bin/activate

# Install dependencies
pip install -r requirements.txt

# Start the ML service
uvicorn app:app --host 0.0.0.0 --port 8000

# In a new terminal, verify the service
curl http://localhost:8000/health
```

**Expected Response:**
```json
{
  "status": "UP"
}
```

### 3. Spring Boot Backend Setup

```bash
# Navigate to backend directory (from project root)
cd customerChurn

# Build the project
./mvnw.cmd clean install

# Run the Spring Boot application
./mvnw.cmd spring-boot:run
```

**Expected Output:**
```
Started CustomerChurnApplication in X.XXX seconds
```

---

## API Documentation

### Swagger/OpenAPI

The Spring Boot application provides interactive Swagger UI documentation:

**Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

**OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

The Swagger interface allows you to:
- View all available endpoints
- See request/response schemas
- Test endpoints directly from the UI
- View HTTP status codes and error responses

### Base URLs

| Service | Base URL |
|---------|----------|
| Spring Boot API | `http://localhost:8080` |
| FastAPI ML Service | `http://localhost:8000` |

---

## Public Endpoints

These endpoints do **NOT** require authentication.

### Health Check

**Endpoint:** `POST /public/health_check`

**Description:** Verify the Spring Boot API is running.

**Authentication:** Not required

**Request:**
```bash
curl -X POST http://localhost:8080/public/health_check
```

**Response:**
```
health check pass
```

**HTTP Status:** 200 OK

---

### Register User

**Endpoint:** `POST /public/saveuser`

**Description:** Create a new user account with authentication credentials.

**Authentication:** Not required

**Request:**
```bash
curl -X POST http://localhost:8080/public/saveuser \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "username": "demo",
    "password": "demo123",
    "email": "demo@example.com",
    "roles": ["USER"]
  }'
```

**Request Body Schema:**
```json
{
  "id": 0,
  "username": "string",
  "password": "string (will be BCrypt encoded)",
  "email": "string",
  "roles": ["USER", "ADMIN"] (optional)
}
```

**Response:**
```
HTTP 201 Created
```

**Response Body:** Empty on success

**Error Responses:**
- `HTTP 400 Bad Request` - Invalid input or user already exists

**Notes:**
- Password is automatically encoded using BCrypt
- Username must be unique
- This endpoint is used to bootstrap the system with the first user

---

## User Endpoints

These endpoints **REQUIRE** HTTP Basic Authentication.

**Authentication Format:** Username and password created via `/public/saveuser`

**Example Header:**
```
Authorization: Basic ZGVtbzpkZW1vMTIz  (Base64 encoded: demo:demo123)
```

### Get Authenticated User Profile

**Endpoint:** `GET /user/getuser`

**Description:** Retrieve the profile of the currently authenticated user.

**Authentication:** Required

**Request:**
```bash
curl -X GET http://localhost:8080/user/getuser \
  -u demo:demo123
```

**Response:**
```json
{
  "id": 1,
  "username": "demo",
  "email": "demo@example.com",
  "roles": ["USER"]
}
```

**HTTP Status:** 200 OK

**Error Responses:**
- `HTTP 401 Unauthorized` - Invalid credentials
- `HTTP 404 Not Found` - User not found

---

### Update User Profile

**Endpoint:** `PUT /user/updateuser`

**Description:** Update email and roles for the currently authenticated user.

**Authentication:** Required

**Request:**
```bash
curl -X PUT http://localhost:8080/user/updateuser \
  -u demo:demo123 \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newemail@example.com",
    "roles": ["USER", "ADMIN"]
  }'
```

**Request Body Schema:**
```json
{
  "email": "string (new email address)",
  "roles": ["USER", "ADMIN"] (optional)
}
```

**Response:**
```
HTTP 204 No Content
```

**Error Responses:**
- `HTTP 400 Bad Request` - Invalid input
- `HTTP 401 Unauthorized` - Invalid credentials

**Notes:**
- Only email and roles can be updated
- Username cannot be changed
- Password is not updated via this endpoint; use `/user/changepassword` instead

---

### Change Password

**Endpoint:** `PUT /user/changepassword`

**Description:** Change the password for the currently authenticated user.

**Authentication:** Required

**Request:**
```bash
curl -X PUT http://localhost:8080/user/changepassword \
  -u demo:demo123 \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "demo123",
    "newPassword": "newPassword456",
    "confirmPassword": "newPassword456"
  }'
```

**Request Body Schema:**
```json
{
  "currentPassword": "string (current password)",
  "newPassword": "string (new password)",
  "confirmPassword": "string (confirmation of new password)"
}
```

**Response:**
```
HTTP 201 Created
```

**Error Responses:**
- `HTTP 304 Not Modified` - Current password incorrect
- `HTTP 304 Not Modified` - New password and confirm password do not match
- `HTTP 401 Unauthorized` - Invalid credentials
- `HTTP 502 Bad Gateway` - Unexpected error

**Notes:**
- Current password must match the user's existing password
- New password and confirmation must match exactly
- All three password fields are required

---

### Delete User Account

**Endpoint:** `DELETE /user/delete_user`

**Description:** Permanently delete the currently authenticated user account.

**Authentication:** Required

**Request:**
```bash
curl -X DELETE http://localhost:8080/user/delete_user \
  -u demo:demo123
```

**Response:**
```
HTTP 204 No Content
```

**Error Responses:**
- `HTTP 401 Unauthorized` - Invalid credentials
- `HTTP 502 Bad Gateway` - Unexpected error

**Notes:**
- This operation is irreversible
- All user data will be permanently deleted from the database
- No confirmation prompt is provided

---

## Customer Endpoints

These endpoints **REQUIRE** HTTP Basic Authentication.

### Get Churn Prediction

**Endpoint:** `POST /customer/getpredection`

**Description:** Submit customer data and receive a churn prediction with probability score.

**Authentication:** Required

**Request:**
```bash
curl -X POST http://localhost:8080/customer/getpredection \
  -u demo:demo123 \
  -H "Content-Type: application/json" \
  -d '{
    "gender": "Male",
    "seniorCitizen": 0,
    "partner": "Yes",
    "dependents": "No",
    "tenure": 1,
    "phoneService": "No",
    "multipleLines": "No phone service",
    "internetService": "DSL",
    "onlineSecurity": "No",
    "onlineBackup": "Yes",
    "deviceProtection": "No",
    "techSupport": "No",
    "streamingTV": "No",
    "streamingMovies": "No",
    "contract": "Month-to-month",
    "paperlessBilling": "Yes",
    "paymentMethod": "Electronic check",
    "monthlyCharges": 29.85,
    "totalCharges": 29.85
  }'
```

**Request Body Schema (Customer Entity):**
```json
{
  "customerId": 0 (optional, auto-generated),
  "gender": "string (Male/Female)",
  "seniorCitizen": 0 or 1,
  "partner": "string (Yes/No)",
  "dependents": "string (Yes/No)",
  "tenure": 0 (integer, months),
  "phoneService": "string (Yes/No)",
  "multipleLines": "string",
  "internetService": "string (DSL/Fiber optic/No)",
  "onlineSecurity": "string (Yes/No/No internet service)",
  "onlineBackup": "string (Yes/No/No internet service)",
  "deviceProtection": "string (Yes/No/No internet service)",
  "techSupport": "string (Yes/No/No internet service)",
  "streamingTV": "string (Yes/No/No internet service)",
  "streamingMovies": "string (Yes/No/No internet service)",
  "contract": "string (Month-to-month/One year/Two year)",
  "paperlessBilling": "string (Yes/No)",
  "paymentMethod": "string (Electronic check/Mailed check/Bank transfer/Credit card)",
  "monthlyCharges": 0.0 (decimal),
  "totalCharges": 0.0 (decimal)
}
```

**Response (Success - HTTP 200 OK):**
```json
{
  "churn": "YES",
  "churnProbability": 0.85
}
```

**Response Schema:**
```json
{
  "churn": "YES or NO",
  "churnProbability": 0.0 - 1.0 (float)
}
```

**Error Responses:**
- `HTTP 400 Bad Request` - Invalid customer data
- `HTTP 401 Unauthorized` - Invalid credentials
- `HTTP 502 Bad Gateway` - ML service error or connection failure

**Example Workflow:**
1. User authenticates with `/public/saveuser`
2. User calls `/customer/getpredection` with customer data
3. Spring Boot receives the request
4. Spring Boot calls FastAPI `/predict` endpoint
5. FastAPI returns prediction result
6. Spring Boot returns prediction to client

**Notes:**
- All customer fields must be populated with valid values
- Field names are case-sensitive (camelCase from Java perspective)
- The FastAPI service performs the actual prediction using scikit-learn
- Churn probability ranges from 0.0 (no churn) to 1.0 (certain churn)

---

## ML Service Endpoints

### FastAPI Health Check

**Endpoint:** `GET /health`

**Base URL:** `http://localhost:8000`

**Description:** Verify the FastAPI ML service is running.

**Authentication:** Not required

**Request:**
```bash
curl http://localhost:8000/health
```

**Response:**
```json
{
  "status": "UP"
}
```

**HTTP Status:** 200 OK

---

### ML Service Prediction

**Endpoint:** `POST /predict`

**Base URL:** `http://localhost:8000`

**Description:** Request churn prediction from the scikit-learn model (called by Spring Boot backend).

**Authentication:** Not required (internal service)

**Request:**
```bash
curl -X POST http://localhost:8000/predict \
  -H "Content-Type: application/json" \
  -d '{
    "gender": "Male",
    "SeniorCitizen": 0,
    "Partner": "Yes",
    "Dependents": "No",
    "tenure": 12,
    "PhoneService": "No",
    "MultipleLines": "No phone service",
    "InternetService": "DSL",
    "OnlineSecurity": "No",
    "OnlineBackup": "Yes",
    "DeviceProtection": "No",
    "TechSupport": "No",
    "StreamingTV": "No",
    "StreamingMovies": "No",
    "Contract": "Month-to-month",
    "PaperlessBilling": "Yes",
    "PaymentMethod": "Electronic check",
    "MonthlyCharges": 29.85,
    "TotalCharges": 357.0
  }'
```

**Request Body Schema (Pydantic Model):**
```json
{
  "customerId": null (optional),
  "gender": "string",
  "SeniorCitizen": integer (0 or 1),
  "Partner": "string (Yes/No)",
  "Dependents": "string (Yes/No)",
  "tenure": integer,
  "PhoneService": "string (Yes/No)",
  "MultipleLines": "string",
  "InternetService": "string (DSL/Fiber optic/No)",
  "OnlineSecurity": "string",
  "OnlineBackup": "string",
  "DeviceProtection": "string",
  "TechSupport": "string",
  "StreamingTV": "string",
  "StreamingMovies": "string",
  "Contract": "string",
  "PaperlessBilling": "string (Yes/No)",
  "PaymentMethod": "string",
  "MonthlyCharges": "decimal/float",
  "TotalCharges": "decimal/float"
}
```

**Response (HTTP 200 OK):**
```json
{
  "churn": "YES",
  "churnProbability": 0.7523
}
```

**Response Schema:**
```json
{
  "churn": "string (YES or NO)",
  "churnProbability": "float (0.0 to 1.0)"
}
```

**Error Responses:**
- `HTTP 422 Unprocessable Entity` - Invalid input data (validation error)
- `HTTP 500 Internal Server Error` - Model loading or prediction error

**Notes:**
- Called internally by Spring Boot's `PredictionService`
- Uses PascalCase for field names (Python convention)
- Model must be present at `Models/churn_model.joblib`
- If model file is missing, run `Model_train.ipynb` to regenerate

---

## Authentication

### HTTP Basic Authentication

All protected endpoints use HTTP Basic Authentication (RFC 7617).

**Credentials:**
- Created via `/public/saveuser` endpoint
- Username and password are sent in the `Authorization` header
- Credentials are Base64 encoded

**Example:**

**Python:**
```python
import requests
from requests.auth import HTTPBasicAuth

response = requests.get(
    'http://localhost:8080/user/getuser',
    auth=HTTPBasicAuth('demo', 'demo123')
)
```

**cURL:**
```bash
curl -u demo:demo123 http://localhost:8080/user/getuser
```

**JavaScript (Fetch API):**
```javascript
const response = await fetch('http://localhost:8080/user/getuser', {
  method: 'GET',
  headers: {
    'Authorization': 'Basic ' + btoa('demo:demo123')
  }
});
```

### Security Configuration

- **Protected Paths:** `/user/**`, `/customer/**`
- **Public Paths:** `/public/**`, `/swagger-ui/**`, `/v3/api-docs/**`
- **CSRF:** Disabled for API usage
- **Password Encoding:** BCrypt with strength 10

---

## Database Schema

### users Table

Backed by `User.java` entity

**Columns:**
| Column | Type | Constraints | Notes |
|--------|------|-----------|-------|
| id | BIGSERIAL | PRIMARY KEY | Auto-generated |
| username | VARCHAR | UNIQUE, NOT NULL | Login identifier |
| password | VARCHAR | NOT NULL | BCrypt encoded |
| email | VARCHAR | UNIQUE, NOT NULL | Contact email |
| roles | VARCHAR | | Comma-separated roles |

**Indexes:**
- PRIMARY KEY on `id`
- UNIQUE on `username`
- UNIQUE on `email`

### customers Table

Backed by `Customer.java` entity

**Demographic Columns:**
| Column | Type | Notes |
|--------|------|-------|
| customer_id | BIGSERIAL | PRIMARY KEY, auto-generated |
| gender | VARCHAR | Male/Female |
| senior_citizen | INT | 0 or 1 |
| partner | VARCHAR | Yes/No |
| dependents | VARCHAR | Yes/No |

**Account Columns:**
| Column | Type | Notes |
|--------|------|-------|
| tenure | INT | Months with company |
| contract | VARCHAR | Month-to-month/One year/Two year |
| payment_method | VARCHAR | Payment type |
| paperless_billing | VARCHAR | Yes/No |

**Service Columns:**
| Column | Type | Notes |
|--------|------|-------|
| phone_service | VARCHAR | Yes/No |
| multiple_lines | VARCHAR | Service type |
| internet_service | VARCHAR | DSL/Fiber optic/No |
| online_security | VARCHAR | Yes/No/No internet service |
| online_backup | VARCHAR | Yes/No/No internet service |
| device_protection | VARCHAR | Yes/No/No internet service |
| tech_support | VARCHAR | Yes/No/No internet service |
| streaming_tv | VARCHAR | Yes/No/No internet service |
| streaming_movies | VARCHAR | Yes/No/No internet service |

**Billing Columns:**
| Column | Type | Notes |
|--------|------|-------|
| monthly_charges | NUMERIC | Monthly bill amount |
| total_charges | NUMERIC | Total amount charged |

**Prediction Columns:**
| Column | Type | Notes |
|--------|------|-------|
| churn | VARCHAR | YES/NO prediction result |
| churn_probability | NUMERIC | Probability score (0.0-1.0) |

**Indexes:**
- PRIMARY KEY on `customer_id`

---

## Running the Application

### Full Start Sequence

#### Terminal 1: FastAPI ML Service

```bash
cd CustomerChurnModel
Churn\Scripts\activate  # or source Churn/bin/activate on Linux/Mac
uvicorn app:app --host 0.0.0.0 --port 8000
```

**Expected Output:**
```
INFO:     Uvicorn running on http://0.0.0.0:8000
INFO:     Application startup complete
```

#### Terminal 2: Spring Boot API

```bash
cd customerChurn
./mvnw.cmd spring-boot:run  # or ./mvnw on Linux/Mac
```

**Expected Output:**
```
Started CustomerChurnApplication in X.XXX seconds
INFO ... : Tomcat started on port(s): 8080 (http)
```

#### Verify All Services

```bash
# Spring Boot Health
curl http://localhost:8080/public/health_check

# FastAPI Health
curl http://localhost:8000/health

# Swagger UI
# Open browser to: http://localhost:8080/swagger-ui/index.html
```

### Docker Deployment

Each service includes a Dockerfile for containerization.

**Build Backend Image:**
```bash
cd customerChurn
docker build -t customer-churn-backend:latest .
```

**Build ML Service Image:**
```bash
cd CustomerChurnModel
docker build -t customer-churn-ml:latest .
```

---

## Project Features

### ✅ Implemented Features

- **User Management**
  - User registration with password encoding (BCrypt)
  - User profile retrieval
  - User profile updates
  - Password change with validation
  - User deletion

- **Authentication & Security**
  - HTTP Basic authentication
  - Spring Security configuration
  - Role-based access control
  - CSRF protection disabled for API
  - Password encoding with BCrypt

- **Customer Churn Prediction**
  - Accept customer data via REST API
  - Call FastAPI ML service for predictions
  - Return churn prediction with probability

- **API Documentation**
  - Swagger UI at `/swagger-ui/index.html`
  - OpenAPI JSON at `/v3/api-docs`
  - Complete endpoint documentation

- **Database Persistence**
  - Spring Data JPA with Hibernate ORM
  - PostgreSQL database
  - Automatic table creation/update (`ddl-auto: update`)
  - User and customer entity management

- **ML Service Integration**
  - FastAPI microservice architecture
  - scikit-learn model loading with joblib
  - Pandas-based data transformation
  - Probability scoring for churn prediction

### 🚀 Suggested Future Enhancements

- Add automated integration tests with TestContainers
- Persist prediction results for analytics and model monitoring
- Docker Compose for full stack orchestration (backend, ML service, PostgreSQL)
- Externalize configuration via environment variables
- Add model retraining workflow and monitoring
- Add request/response logging middleware
- Implement pagination for list endpoints
- Add batch prediction support
- Customer data persistence in database
- API rate limiting
- Automated model versioning

---

## File References

For more detailed information, refer to the documentation files:

- **API Details:** [docs/API.md](docs/API.md)
- **Architecture Deep Dive:** [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)
- **Setup Guide:** [docs/SETUP.md](docs/SETUP.md)
- **ML Workflow:** [docs/ML_WORKFLOW.md](docs/ML_WORKFLOW.md)
- **Portfolio Notes:** [docs/RESUME_NOTES.md](docs/RESUME_NOTES.md)

---

## Support

For issues or questions:

1. Check the API documentation in Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
2. Review the detailed documentation in the `docs/` folder
3. Verify all services are running (health checks)
4. Check PostgreSQL connection and database creation
5. Review application logs in Spring Boot and FastAPI console output

---

## License

This project is provided as-is for portfolio and educational purposes.

---

**Last Updated:** June 9, 2026

**Version:** 1.0.0

**Status:** Production Ready
