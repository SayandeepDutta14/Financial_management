package com.finance.dashboard.service;

import com.finance.dashboard.dto.DashboardSummaryResponse;
import com.finance.dashboard.dto.DashboardSummaryResponse.MonthlyTrend;
import com.finance.dashboard.dto.FinancialRecordResponse;
import com.finance.dashboard.model.TransactionType;
import com.finance.dashboard.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository recordRepository;

    public DashboardSummaryResponse getSummary() {
        BigDecimal totalIncome = recordRepository.sumByType(TransactionType.INCOME);
        BigDecimal totalExpenses = recordRepository.sumByType(TransactionType.EXPENSE);
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        Map<String, BigDecimal> incomeByCategory = toMap(
            recordRepository.sumByCategory(TransactionType.INCOME));
        Map<String, BigDecimal> expensesByCategory = toMap(
            recordRepository.sumByCategory(TransactionType.EXPENSE));

        List<MonthlyTrend> trends = buildMonthlyTrends(recordRepository.monthlyTrends());

        List<FinancialRecordResponse> recent = recordRepository
            .findRecentActivity(PageRequest.of(0, 10))
            .stream()
            .map(FinancialRecordResponse::from)
            .toList();

        return DashboardSummaryResponse.builder()
            .totalIncome(totalIncome)
            .totalExpenses(totalExpenses)
            .netBalance(netBalance)
            .incomeByCategory(incomeByCategory)
            .expensesByCategory(expensesByCategory)
            .monthlyTrends(trends)
            .recentActivity(recent)
            .build();
    }

    private Map<String, BigDecimal> toMap(List<Object[]> rows) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            map.put((String) row[0], (BigDecimal) row[1]);
        }
        return map;
    }

    private List<MonthlyTrend> buildMonthlyTrends(List<Object[]> rows) {
        // rows: [month_str, type_str, amount]
        Map<String, MonthlyTrendAccumulator> acc = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String month = (String) row[0];
            String type  = (String) row[1];
            BigDecimal amount = (BigDecimal) row[2];
            acc.computeIfAbsent(month, k -> new MonthlyTrendAccumulator(k));
            if (TransactionType.INCOME.name().equals(type)) {
                acc.get(month).income = amount;
            } else {
                acc.get(month).expenses = amount;
            }
        }
        return acc.values().stream().map(a -> MonthlyTrend.builder()
            .month(a.month)
            .income(a.income)
            .expenses(a.expenses)
            .net(a.income.subtract(a.expenses))
            .build()).toList();
    }

    private static class MonthlyTrendAccumulator {
        String month;
        BigDecimal income   = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;
        MonthlyTrendAccumulator(String month) { this.month = month; }
    }
}
