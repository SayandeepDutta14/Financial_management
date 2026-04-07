package com.finance.dashboard.dto;

import com.finance.dashboard.model.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FinancialRecordRequest {
    @NotNull @DecimalMin("0.01") private BigDecimal amount;
    @NotNull private TransactionType type;
    @NotBlank private String category;
    @NotNull private LocalDate date;
    @Size(max = 500) private String notes;
}
