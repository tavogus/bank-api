package com.bank.api.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.api.dto.InvoiceDTO;
import com.bank.api.dto.TransactionResponseDTO;
import com.bank.api.entity.Account;
import com.bank.api.entity.Card;
import com.bank.api.entity.Invoice;
import com.bank.api.entity.InvoiceStatus;
import com.bank.api.entity.PaymentType;
import com.bank.api.entity.Transaction;
import com.bank.api.entity.TransactionType;
import com.bank.api.repository.InvoiceRepository;

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
        // Cria uma nova fatura para o cartão
        Invoice invoice = new Invoice();
        invoice.setCard(card);
        invoice.setStatus(InvoiceStatus.OPEN);
        
        // Define a data de fechamento como dia 5 do próximo mês
        LocalDate today = LocalDate.now();
        LocalDate closingDate = today.withDayOfMonth(5).plusMonths(1);
        invoice.setClosingDate(closingDate);
        
        // Define a data de vencimento como dia 15 do próximo mês
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

        // Busca a fatura aberta do cartão ou cria uma nova
        Invoice invoice = invoiceRepository.findByCardIdAndStatus(card.getId(), InvoiceStatus.OPEN);
        if (invoice == null) {
            invoice = createInvoice(card);
        }

        // Adiciona a transação à fatura
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

        // Debita o valor da fatura da conta
        account.setBalance(account.getBalance().subtract(invoice.getTotalAmount()));
        accountService.save(account);

        // Atualiza o status da fatura
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
        return invoiceRepository.findByCardId(cardId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private InvoiceDTO toDTO(Invoice invoice) {
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
            transaction.getDescription(),
            transaction.getCreatedAt()
        );
    }

    // Método para ser executado diariamente para verificar faturas vencidas
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