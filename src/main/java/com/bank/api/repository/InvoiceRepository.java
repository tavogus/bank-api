package com.bank.api.repository;

import com.bank.api.entity.Invoice;
import com.bank.api.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByCardIdOrderByClosingDateDesc(Long cardId);
    Invoice findByCardIdAndStatus(Long cardId, InvoiceStatus status);
    List<Invoice> findByDueDateBeforeAndStatus(LocalDate date, InvoiceStatus status);
} 