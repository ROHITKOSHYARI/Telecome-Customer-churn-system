import importlib
import sys

import pytest
from fastapi.testclient import TestClient


class FakeModel:
    def predict(self, data):
        assert list(data.columns) == [
            "gender",
            "SeniorCitizen",
            "Partner",
            "Dependents",
            "tenure",
            "PhoneService",
            "MultipleLines",
            "InternetService",
            "OnlineSecurity",
            "OnlineBackup",
            "DeviceProtection",
            "TechSupport",
            "StreamingTV",
            "StreamingMovies",
            "Contract",
            "PaperlessBilling",
            "PaymentMethod",
            "MonthlyCharges",
            "TotalCharges",
        ]
        return ["Yes"]

    def predict_proba(self, data):
        return [[0.22, 0.78]]


@pytest.fixture
def client(monkeypatch):
    import joblib

    monkeypatch.setattr(joblib, "load", lambda path: FakeModel())
    sys.modules.pop("app", None)
    app_module = importlib.import_module("app")
    return TestClient(app_module.app)


def test_health_returns_up(client):
    response = client.get("/health")

    assert response.status_code == 200
    assert response.json() == {"status": "UP"}


def test_predict_returns_churn_prediction(client):
    response = client.post(
        "/predict",
        json={
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
            "TotalCharges": 1069.20,
        },
    )

    assert response.status_code == 200
    assert response.json() == {"churn": "Yes", "churnProbability": 0.78}


def test_predict_validates_required_fields(client):
    response = client.post("/predict", json={"gender": "Female"})

    assert response.status_code == 422
