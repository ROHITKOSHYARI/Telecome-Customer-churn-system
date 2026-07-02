# Resume Notes

Use this file to describe the project clearly in resumes, interviews, and portfolio pages.

## Short Resume Version

Built an end-to-end customer churn prediction platform using Spring Boot, PostgreSQL, FastAPI, and scikit-learn. Implemented secured REST APIs, user management with BCrypt authentication, ML model serving through a Python microservice, and a documented API workflow for real-time churn probability scoring.

## Detailed Resume Version

Developed a customer churn prediction system with a Spring Boot backend and FastAPI machine learning service. Designed REST APIs for user registration, JWT-authenticated profile management, and churn prediction. Integrated PostgreSQL through Spring Data JPA, configured Spring Security with JWT authentication and BCrypt password hashing, and connected the Java backend to a Python scikit-learn inference service using RestTemplate. Trained a logistic regression pipeline with one-hot encoding and feature scaling on the Telco Customer Churn dataset, serialized the model with joblib, and exposed real-time prediction results with churn probability scores.

## Bullet Points

- Built a full-stack backend and ML microservice architecture for customer churn prediction using Spring Boot, FastAPI, PostgreSQL, and scikit-learn.
- Implemented secured REST APIs with Spring Security, JWT authentication, BCrypt password hashing, and role-based user records.
- Trained and served a scikit-learn logistic regression pipeline with categorical encoding, numeric scaling, and churn probability scoring.
- Integrated Java and Python services through REST, allowing the Spring Boot API to request predictions from a dedicated FastAPI model service.
- Documented setup, architecture, API contracts, model workflow, and deployment steps for a portfolio-ready project.

## Interview Talking Points

### Why two services?

The Java backend handles API, security, and persistence concerns, while the Python service handles ML inference using the Python data science ecosystem. This mirrors common production patterns where model serving is separated from core application logic.

### Why logistic regression?

Logistic regression is a strong baseline for binary classification. It is interpretable, works well with encoded categorical features, and produces probabilities that are useful for churn risk scoring.

### Why probability instead of only yes/no?

A churn probability lets a business prioritize retention actions. For example, customers with high churn probability can be targeted with offers, support follow-ups, or contract upgrades.

### What would you improve next?

- Add automated integration tests for the prediction flow.
- Persist prediction results for analytics.
- Add Docker Compose for backend, ML service, and PostgreSQL.
- Externalize service URLs and database credentials through environment variables.
- Add model monitoring and retraining workflow.

## Portfolio Description

Customer Churn Prediction is an end-to-end backend and machine learning project that predicts whether a telecom customer is likely to churn. The system combines a secured Spring Boot API, PostgreSQL persistence, and a FastAPI model-serving layer. The ML pipeline preprocesses customer account features, generates a churn prediction, and returns a probability score that can help prioritize customer retention decisions.

## Suggested GitHub Topics

```text
spring-boot
fastapi
machine-learning
customer-churn
postgresql
scikit-learn
rest-api
java
python
portfolio-project
```
