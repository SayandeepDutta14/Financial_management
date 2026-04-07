package com.finance.dashboard.service;

import com.finance.dashboard.dto.*;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.model.*;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;
    private final UserRepository userRepository;

    public PagedResponse<FinancialRecordResponse> getRecords(
            TransactionType type, String category,
            LocalDate from, LocalDate to,
            int page, int size, String sortBy, String dir) {

        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<FinancialRecord> records = recordRepository.findWithFilters(type, category, from, to, pageable);
        Page<FinancialRecordResponse> mapped = records.map(FinancialRecordResponse::from);
        return PagedResponse.from(mapped);
    }

    public FinancialRecordResponse getById(Long id) {
        return FinancialRecordResponse.from(findActiveOrThrow(id));
    }

    @Transactional
    public FinancialRecordResponse create(FinancialRecordRequest request) {
        User currentUser = currentUser();
        FinancialRecord record = FinancialRecord.builder()
            .amount(request.getAmount())
            .type(request.getType())
            .category(request.getCategory())
            .date(request.getDate())
            .notes(request.getNotes())
            .createdBy(currentUser)
            .deleted(false)
            .build();
        return FinancialRecordResponse.from(recordRepository.save(record));
    }

    @Transactional
    public FinancialRecordResponse update(Long id, FinancialRecordRequest request) {
        FinancialRecord record = findActiveOrThrow(id);
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNotes(request.getNotes());
        return FinancialRecordResponse.from(recordRepository.save(record));
    }

    @Transactional
    public void delete(Long id) {
        FinancialRecord record = findActiveOrThrow(id);
        record.setDeleted(true); // soft delete
        recordRepository.save(record);
    }

    private FinancialRecord findActiveOrThrow(Long id) {
        return recordRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Financial record not found with id: " + id));
    }

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
