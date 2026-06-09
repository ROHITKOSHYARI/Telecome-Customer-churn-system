# Customer Churn Prediction

A portfolio-ready customer churn prediction system that combines a Java Spring Boot REST API with a Python FastAPI machine learning inference service. The backend handles user registration, authentication, persistence, and prediction routing, while the ML service loads a trained scikit-learn pipeline and returns churn predictions with probability scores.

Quick docs: [Setup Guide](./docs/SETUP.md) | [API Documentation](./docs/API.md) | [Architecture](./docs/ARCHITECTURE.md) | [Machine Learning Workflow](./docs/ML_WORKFLOW.md) | [Resume Notes](./docs/RESUME_NOTES.md)

## Project Highlights

- End-to-end customer churn prediction workflow using the Telco Customer Churn dataset.
- Spring Boot 3.5 backend with REST APIs, Spring Security, Spring Data JPA, PostgreSQL, and Swagger/OpenAPI.
- FastAPI inference microservice that serves a serialized scikit-learn model.
- ML pipeline using preprocessing, one-hot encoding, numeric scaling, and logistic regression.
- Secure user APIs with HTTP Basic authentication and BCrypt password hashing.
- Container-ready Spring Boot service through a production-style Dockerfile.

## Tech Stack

| Layer | Technologies |
| --- | --- |
| Backend API | Java 21, Spring Boot 3.5, Spring Web, Spring Security |
| Persistence | PostgreSQL, Spring Data JPA, Hibernate |
| ML Service | Python, FastAPI, Pydantic, pandas, joblib |
| Machine Learning | scikit-learn, Logistic Regression, OneHotEncoder, StandardScaler |
| Documentation | Swagger UI, OpenAPI, Markdown |
| Build/Deploy | Maven Wrapper, Docker |

## Repository Structure

```text
.
├── customerChurn/                 # Spring Boot backend API
│   ├── src/main/java/com/customerChurn/
│   │   ├── config/                # Security and RestTemplate configuration
│   │   ├── controller/            # Public, user, and prediction controllers
│   │   ├── dto/                   # Request/response DTOs
│   │   ├── entity/                # JPA entities
│   │   ├── repository/            # Spring Data repositories
│   │   └── service/               # Business logic and ML API integration
│   ├── src/main/resources/
│   │   ├── application.yaml       # PostgreSQL and Spring settings
│   │   └── logback.xml            # Logging configuration
│   ├── Dockerfile                 # Spring Boot container image
│   └── pom.xml                    # Java dependencies
├── CustomerChurnModel/            # Python ML training and serving layer
│   ├── app.py                     # FastAPI prediction service
│   ├── Model_train.ipynb          # Model training notebook
│   ├── Data/                      # Telco customer churn dataset
│   └── Models/                    # Serialized model artifacts
└── docs/                          # Detailed project documentation
```

## How It Works

1. A user registers through the public Spring Boot API.
2. Secured endpoints use HTTP Basic authentication.
3. A customer profile is submitted to the Spring Boot prediction endpoint.
4. Spring Boot maps the request into the exact feature names expected by the ML model.
5. The backend calls the FastAPI service at `http://localhost:8000/predict`.
6. FastAPI loads `Models/churn_model.joblib`, performs inference, and returns:
   - `churn`
   - `churnProbability`
7. Spring Boot returns the prediction response to the client.

## Prerequisites

- Java 21
- Maven, or the included Maven Wrapper
- Python 3.11 or newer recommended
- PostgreSQL
- Git
- Docker optional

## Quick Start

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd Customerchurnpredection
```

### 2. Configure PostgreSQL

Create a PostgreSQL database:

```sql
CREATE DATABASE customer_churn;
```

The current backend configuration expects:

```yaml
url: jdbc:postgresql://localhost:5432/customer_churn
username: postgres
password: root
```

Update `customerChurn/src/main/resources/application.yaml` if your local credentials are different.

### 3. Start the ML Inference Service

```bash
cd CustomerChurnModel
python -m venv .venv
.venv\Scripts\activate
pip install fastapi uvicorn pandas scikit-learn joblib pydantic
uvicorn app:app --host 0.0.0.0 --port 8000
```

Health check:

```bash
curl http://localhost:8000/health
```

Expected response:

```json
{
  "status": "UP"
}
```

### 4. Start the Spring Boot Backend

```bash
cd customerChurn
.\mvnw.cmd spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

## Main API Endpoints

| Method | Endpoint | Auth | Description |
| --- | --- | --- | --- |
| POST | `/public/health_check` | No | Backend health check |
| POST | `/public/saveuser` | No | Register a new user |
| GET | `/user/getuser` | Yes | Fetch authenticated user profile |
| DELETE | `/user/delete_user` | Yes | Delete authenticated user |
| POST | `/customer/getpredection` | Yes | Predict customer churn |
| GET | `http://localhost:8000/health` | No | ML service health check |
| POST | `http://localhost:8000/predict` | No | Direct ML prediction endpoint |

The backend endpoint name is currently spelled `/customer/getpredection` in code, so API clients must use that exact path unless the controller is renamed later.

## Example Prediction Request

```json
{
  "gender": "Female",
  "seniorCitizen": 0,
  "partner": "Yes",
  "dependents": "No",
  "tenure": 12,
  "phoneService": "Yes",
  "multipleLines": "No",
  "internetService": "Fiber optic",
  "onlineSecurity": "No",
  "onlineBackup": "Yes",
  "deviceProtection": "No",
  "techSupport": "No",
  "streamingTV": "Yes",
  "streamingMovies": "Yes",
  "contract": "Month-to-month",
  "paperlessBilling": "Yes",
  "paymentMethod": "Electronic check",
  "monthlyCharges": 89.10,
  "totalCharges": 1069.20
}
```

Example response:

```json
{
  "churn": "Yes",
  "churnProbability": 0.78
}
```

## Documentation

Detailed documentation is available in:

- [Setup Guide](./docs/SETUP.md)
- [API Documentation](./docs/API.md)
- [Architecture](./docs/ARCHITECTURE.md)
- [Machine Learning Workflow](./docs/ML_WORKFLOW.md)
- [Resume Notes](./docs/RESUME_NOTES.md)

## Testing

Run the Spring Boot test suite:

```bash
cd customerChurn
.\mvnw.cmd test
```

The current test suite includes a Spring context load test.

## Docker

Build the Spring Boot JAR:

```bash
cd customerChurn
.\mvnw.cmd clean package
```

Build the Docker image:

```bash
docker build -t customer-churn-api .
```

Run the container:

```bash
docker run -p 8080:8080 customer-churn-api
```

The containerized backend still expects PostgreSQL and the FastAPI model service to be reachable from its runtime environment.

## Resume Summary

Built an end-to-end customer churn prediction platform using Spring Boot, PostgreSQL, and FastAPI. Designed secured REST APIs, integrated a trained scikit-learn model through a microservice architecture, implemented user management with Spring Security and BCrypt, and documented deployment, API usage, and ML workflow for production-style project presentation.
