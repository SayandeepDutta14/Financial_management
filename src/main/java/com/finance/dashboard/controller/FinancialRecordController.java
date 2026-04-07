package com.finance.dashboard.controller;

import com.finance.dashboard.dto.*;
import com.finance.dashboard.model.TransactionType;
import com.finance.dashboard.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    /**
     * GET /api/records
     * Accessible by: VIEWER, ANALYST, ADMIN
     * Supports filtering by type, category, date range + pagination + sorting
     */
    @GetMapping
    public ResponseEntity<PagedResponse<FinancialRecordResponse>> getRecords(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "20")  int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String dir) {

        return ResponseEntity.ok(
            recordService.getRecords(type, category, from, to, page, size, sortBy, dir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialRecordResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(recordService.getById(id));
    }

    /**
     * POST /api/records
     * Accessible by: ANALYST, ADMIN
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
    public ResponseEntity<FinancialRecordResponse> create(
            @Valid @RequestBody FinancialRecordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recordService.create(request));
    }

    /**
     * PUT /api/records/{id}
     * Accessible by: ANALYST, ADMIN
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ANALYST','ADMIN')")
    public ResponseEntity<FinancialRecordResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FinancialRecordRequest request) {
        return ResponseEntity.ok(recordService.update(id, request));
    }

    /**
     * DELETE /api/records/{id}  — soft delete
     * Accessible by: ADMIN only
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
