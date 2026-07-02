import joblib
from pydantic import BaseModel
from decimal import Decimal
from enum import Enum
from fastapi import FastAPI
import pandas as pd

app = FastAPI()

class Churn(str, Enum):
    YES = "YES"



class Customer(BaseModel):
    customerId: int | None = None
    gender: str
    SeniorCitizen: int
    Partner: str
    Dependents: str
    tenure: int
    PhoneService: str
    MultipleLines: str
    InternetService: str
    OnlineSecurity: str
    OnlineBackup: str
    DeviceProtection: str
    TechSupport: str
    StreamingTV: str
    StreamingMovies: str
    Contract: str
    PaperlessBilling: str
    PaymentMethod: str
    MonthlyCharges: Decimal
    TotalCharges: Decimal
    churn: Churn | None = None
    churnProbability: Decimal | None = None


class PredictionResponse(BaseModel):
    churn: str
    churnProbability: float


@app.get("/health")
def health():
    return {"status": "UP"}

model = joblib.load("Models/churn_model.joblib")
@app.post("/predict", response_model=PredictionResponse)
def predict_customer(customer: Customer):
    data = pd.DataFrame([
        customer.model_dump(
            exclude={"customerId", "churn", "churnProbability"}
        )
    ])

    print(data.columns.tolist())

    prediction = model.predict(data)[0]

    yes_probability = model.predict_proba(data)[0][1]

    return PredictionResponse(
        churn=prediction,
        churnProbability=float(yes_probability)
    )



# docker run -p 8000:8000 churn-ml