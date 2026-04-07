package com.finance.dashboard.dto;

import com.finance.dashboard.model.FinancialRecord;
import com.finance.dashboard.model.TransactionType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FinancialRecordResponse {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private LocalDate date;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FinancialRecordResponse from(FinancialRecord r) {
        FinancialRecordResponse dto = new FinancialRecordResponse();
        dto.id = r.getId();
        dto.amount = r.getAmount();
        dto.type = r.getType();
        dto.category = r.getCategory();
        dto.date = r.getDate();
        dto.notes = r.getNotes();
        dto.createdBy = r.getCreatedBy().getUsername();
        dto.createdAt = r.getCreatedAt();
        dto.updatedAt = r.getUpdatedAt();
        return dto;
    }
}
