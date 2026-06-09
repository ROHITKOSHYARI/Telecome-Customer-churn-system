# Machine Learning Workflow

## Dataset

The project uses the Telco Customer Churn dataset:

```text
CustomerChurnModel/Data/WA_Fn-UseC_-Telco-Customer-Churn.csv
```

The dataset contains customer demographics, account details, service subscriptions, billing information, and the target churn label.

## Training Notebook

Model training is documented in:

```text
CustomerChurnModel/Model_train.ipynb
```

The notebook performs:

- Dataset loading with pandas.
- Feature and target separation.
- Train/test split.
- Categorical preprocessing with `OneHotEncoder`.
- Numeric preprocessing with `StandardScaler`.
- Model training with `LogisticRegression`.
- Classification evaluation.
- Model serialization with joblib.

## Features Used

Categorical features:

- `gender`
- `Partner`
- `Dependents`
- `PhoneService`
- `MultipleLines`
- `InternetService`
- `OnlineSecurity`
- `OnlineBackup`
- `DeviceProtection`
- `TechSupport`
- `StreamingTV`
- `StreamingMovies`
- `Contract`
- `PaperlessBilling`
- `PaymentMethod`

Numeric features:

- `SeniorCitizen`
- `tenure`
- `MonthlyCharges`
- `TotalCharges`

Target:

- `Churn`

## Model Pipeline

The trained model is a scikit-learn pipeline:

```text
ColumnTransformer
  ├── OneHotEncoder for categorical features
  └── StandardScaler for numeric features

LogisticRegression
```

The logistic regression model uses:

```text
max_iter=5000
random_state=42
class_weight="balanced"
```

`class_weight="balanced"` helps account for churn class imbalance.

## Evaluation

The notebook prints a classification report and calculates prediction probabilities. The recorded notebook output shows overall test accuracy around `0.73` on a test set of `1407` records.

For a portfolio presentation, discuss accuracy together with recall, precision, and churn probability because churn prediction is usually more useful as a risk-scoring problem than a strict yes/no classifier.

## Model Artifact

The notebook saves the trained pipeline to:

```text
CustomerChurnModel/Models/churn_model.joblib
```

The FastAPI service loads this artifact:

```python
model = joblib.load("Models/churn_model.joblib")
```

Start FastAPI from inside `CustomerChurnModel` so the relative model path resolves correctly.

## Inference Flow

1. FastAPI receives customer data through `/predict`.
2. Pydantic validates the request body.
3. The request is converted to a pandas DataFrame.
4. The serialized scikit-learn pipeline applies preprocessing.
5. The model returns a churn class using `predict`.
6. The model returns churn probability using `predict_proba`.
7. FastAPI returns a JSON response.

## Production Improvements

Potential next improvements:

- Add model version metadata to prediction responses.
- Move the model path into configuration.
- Add request validation for accepted category values.
- Add model monitoring for prediction drift.
- Save prediction records in PostgreSQL for audit and analysis.
- Compare logistic regression with tree-based models such as Random Forest, XGBoost, or LightGBM.

