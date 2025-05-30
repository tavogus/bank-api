package com.bank.api.service;

import com.bank.api.dto.TransactionRequestDTO;
import com.bank.api.dto.TransactionResponseDTO;
import com.bank.api.entity.*;
import com.bank.api.exception.BusinessException;
import com.bank.api.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final InvoiceService invoiceService;

    public TransactionService(TransactionRepository transactionRepository, 
                            AccountService accountService,
                            InvoiceService invoiceService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.invoiceService = invoiceService;
    }

    @Transactional
    public TransactionResponseDTO transfer(TransactionRequestDTO request) {
        // Find accounts
        Account sourceAccount = getAccount();
        
        Account destinationAccount = accountService.findByAccountNumber(request.destinationAccountNumber())
                .orElseThrow(() -> new BusinessException("Destination account not found"));


        accountService.validateBalance(sourceAccount, request.amount());

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setAmount(request.amount());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setPaymentType(PaymentType.TRANSFER);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setDescription(request.description());

        try {
            // Update balances
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.amount()));
            destinationAccount.setBalance(destinationAccount.getBalance().add(request.amount()));

            // Save accounts
            accountService.save(sourceAccount);
            accountService.save(destinationAccount);

            // Update transaction status
            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction = transactionRepository.save(transaction);

            return mapToResponseDTO(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new BusinessException("Transaction failed: " + e.getMessage());
        }
    }

    public Page<TransactionResponseDTO> getTransactionsByUserId(Pageable pageable) {
        Account account = getAccount();
        
        Page<Transaction> transactions = transactionRepository.findAllTransactionsByAccount(account, pageable);
        
        return transactions.map(this::mapToResponseDTO);
    }

    @Transactional
    public TransactionResponseDTO createCreditCardTransaction(Account account, Card card, BigDecimal amount, String description, PaymentType paymentType) {
        Transaction transaction = new Transaction();
        transaction.setSourceAccount(account);
        transaction.setDestinationAccount(account);
        transaction.setAmount(amount);
        transaction.setPaymentType(paymentType);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setDescription(description);

        try {
            if (paymentType == PaymentType.DEBIT) {
                accountService.validateBalance(account, amount);
                account.setBalance(account.getBalance().subtract(amount));
                accountService.save(account);

                transaction.setType(TransactionType.DEBIT_CARD);
            } else {
                // if credit, add to invoice
                transaction.setType(TransactionType.CREDIT_CARD);
                invoiceService.addTransactionToInvoice(transaction, card);
            }

            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction = transactionRepository.save(transaction);

            return mapToResponseDTO(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new BusinessException("Card purchase failed: " + e.getMessage());
        }
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getSourceAccount().getAccountNumber(),
                transaction.getDestinationAccount().getAccountNumber(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getType(),
                transaction.getPaymentType(),
                transaction.getDescription(),
                transaction.getCreatedAt());
    }

    private static Account getAccount() {
        User user = UserContextService.getCurrentUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (user.getAccount() == null) {
            throw new RuntimeException("Account not found");
        }
        return user.getAccount();
    }
} 