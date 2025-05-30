package com.bank.api.service;

import com.bank.api.dto.InvoiceDTO;
import com.bank.api.dto.TransactionResponseDTO;
import com.bank.api.entity.*;
import com.bank.api.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final AccountService accountService;

    public InvoiceService(InvoiceRepository invoiceRepository,
                         AccountService accountService) {
        this.invoiceRepository = invoiceRepository;
        this.accountService = accountService;
    }

    @Transactional
    public Invoice createInvoice(Card card) {
        // add new invoice to card
        Invoice invoice = new Invoice();
        invoice.setCard(card);
        invoice.setStatus(InvoiceStatus.OPEN);
        
        // set a new closing date like the 5th of next month
        LocalDate today = LocalDate.now();
        LocalDate closingDate = today.withDayOfMonth(5).plusMonths(1);
        invoice.setClosingDate(closingDate);

        // set a new date of overdue like the 15th of next month
        LocalDate dueDate = today.withDayOfMonth(15).plusMonths(1);
        invoice.setDueDate(dueDate);
        
        invoice.setTotalAmount(BigDecimal.ZERO);
        
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public void addTransactionToInvoice(Transaction transaction, Card card) {
        if (transaction.getType() != TransactionType.CREDIT_CARD || 
            transaction.getPaymentType() != PaymentType.CREDIT) {
            return;
        }

        // find open invoice or create a new one
        Invoice invoice = invoiceRepository.findByCardIdAndStatus(card.getId(), InvoiceStatus.OPEN);
        if (invoice == null) {
            invoice = createInvoice(card);
        }

        // add transaction to invoice
        transaction.setInvoice(invoice);
        invoice.getTransactions().add(transaction);
        invoice.setTotalAmount(invoice.getTotalAmount().add(transaction.getAmount()));
        
        invoiceRepository.save(invoice);
    }

    @Transactional
    public void payInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.CLOSED) {
            throw new RuntimeException("Invoice must be closed to be paid");
        }

        Account account = invoice.getCard().getUser().getAccount();
        accountService.validateBalance(account, invoice.getTotalAmount());

        // debit value from invoice's account
        account.setBalance(account.getBalance().subtract(invoice.getTotalAmount()));
        accountService.save(account);

        // update invoice's status
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);
    }

    @Transactional
    public void closeInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.OPEN) {
            throw new RuntimeException("Only open invoices can be closed");
        }

        invoice.setStatus(InvoiceStatus.CLOSED);
        invoiceRepository.save(invoice);
    }

    public List<InvoiceDTO> getCardInvoices(Long cardId) {
        return invoiceRepository.findByCardIdOrderByClosingDateDesc(cardId)
                .stream()
                .map(this::toInvooiceDTO)
                .collect(Collectors.toList());
    }

    private InvoiceDTO toInvooiceDTO(Invoice invoice) {
        return new InvoiceDTO(
            invoice.getId(),
            invoice.getCard().getId(),
            invoice.getDueDate(),
            invoice.getClosingDate(),
            invoice.getTotalAmount(),
            invoice.getStatus(),
            invoice.getTransactions().stream()
                .map(this::mapToTransactionDTO)
                .collect(Collectors.toList())
        );
    }

    private TransactionResponseDTO mapToTransactionDTO(Transaction transaction) {
        return new TransactionResponseDTO(
            transaction.getId(),
            transaction.getSourceAccount().getAccountNumber(),
            transaction.getDestinationAccount().getAccountNumber(),
            transaction.getAmount(),
            transaction.getStatus(),
            transaction.getType(),
            transaction.getPaymentType(),
            transaction.getDescription(),
            transaction.getCreatedAt()
        );
    }

    // method to be excecuted everyday to check overdue invoices
    @Transactional
    public void checkOverdueInvoices() {
        List<Invoice> overdueInvoices = invoiceRepository
            .findByDueDateBeforeAndStatus(LocalDate.now(), InvoiceStatus.CLOSED);
        
        for (Invoice invoice : overdueInvoices) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
            invoiceRepository.save(invoice);
        }
    }
} 