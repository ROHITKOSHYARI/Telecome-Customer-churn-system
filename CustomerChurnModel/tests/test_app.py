import sys
from pathlib import Path

import pytest
from fastapi.testclient import TestClient

# Add project root to Python path
sys.path.append(str(Path(__file__).resolve().parent.parent))


class FakeModel:
    def predict(self, data):
        return ["Yes"]

    def predict_proba(self, data):
        return [[0.22, 0.78]]


@pytest.fixture
def client(monkeypatch):
    # Mock model loading before importing app
    monkeypatch.setattr(
        "joblib.load",
        lambda _: FakeModel()
    )

    # Import app after mocking
    if "app" in sys.modules:
        del sys.modules["app"]

    import app

    return TestClient(app.app)


def test_health_endpoint(client):
    response = client.get("/health")

    assert response.status_code == 200
    assert response.json() == {
        "status": "UP"
    }


def test_predict_endpoint(client):
    payload = {
        "gender": "Male",
        "SeniorCitizen": 0,
        "Partner": "Yes",
        "Dependents": "No",
        "tenure": 24,
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
        "MonthlyCharges": 89.5,
        "TotalCharges": 2148.0
    }

    response = client.post(
        "/predict",
        json=payload
    )

    assert response.status_code == 200

    body = response.json()

    assert body["churn"] == "Yes"
    assert body["churnProbability"] == 0.78


def test_predict_validation_error(client):
    payload = {
        "gender": "Male"
    }

    response = client.post(
        "/predict",
        json=payload
    )

    assert response.status_code == 422