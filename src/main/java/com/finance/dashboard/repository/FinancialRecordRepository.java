package com.finance.dashboard.repository;

import com.finance.dashboard.model.FinancialRecord;
import com.finance.dashboard.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    Optional<FinancialRecord> findByIdAndDeletedFalse(Long id);

    Page<FinancialRecord> findByDeletedFalse(Pageable pageable);

    @Query("""
        SELECT r FROM FinancialRecord r
        WHERE r.deleted = false
          AND (:type IS NULL OR r.type = :type)
          AND (:category IS NULL OR LOWER(r.category) = LOWER(:category))
          AND (:from IS NULL OR r.date >= :from)
          AND (:to IS NULL OR r.date <= :to)
    """)
    Page<FinancialRecord> findWithFilters(
        @Param("type") TransactionType type,
        @Param("category") String category,
        @Param("from") LocalDate from,
        @Param("to") LocalDate to,
        Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r WHERE r.deleted = false AND r.type = :type")
    BigDecimal sumByType(@Param("type") TransactionType type);

    @Query("""
        SELECT r.category, SUM(r.amount)
        FROM FinancialRecord r
        WHERE r.deleted = false AND r.type = :type
        GROUP BY r.category
        ORDER BY SUM(r.amount) DESC
    """)
    List<Object[]> sumByCategory(@Param("type") TransactionType type);

    @Query("""
        SELECT STRFTIME('%Y-%m', r.date) as month, r.type, SUM(r.amount)
        FROM FinancialRecord r
        WHERE r.deleted = false
        GROUP BY month, r.type
        ORDER BY month DESC
    """)
    List<Object[]> monthlyTrends();

    @Query("""
        SELECT r FROM FinancialRecord r
        WHERE r.deleted = false
        ORDER BY r.date DESC, r.createdAt DESC
    """)
    List<FinancialRecord> findRecentActivity(Pageable pageable);
}
