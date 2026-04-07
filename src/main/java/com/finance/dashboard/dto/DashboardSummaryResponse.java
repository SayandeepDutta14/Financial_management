package com.finance.dashboard.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data @Builder
public class DashboardSummaryResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
    private Map<String, BigDecimal> incomeByCategory;
    private Map<String, BigDecimal> expensesByCategory;
    private List<MonthlyTrend> monthlyTrends;
    private List<FinancialRecordResponse> recentActivity;

    @Data @Builder
    public static class MonthlyTrend {
        private String month;
        private BigDecimal income;
        private BigDecimal expenses;
        private BigDecimal net;
    }
}
