package dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PredectionResponse {
    private String churn;
    private BigDecimal churnProbability;
}
