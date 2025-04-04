package com.bank.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.api.dto.TransactionRequestDTO;
import com.bank.api.dto.TransactionResponseDTO;
import com.bank.api.entity.Account;
import com.bank.api.entity.Transaction;
import com.bank.api.entity.TransactionStatus;
import com.bank.api.entity.TransactionType;
import com.bank.api.entity.User;
import com.bank.api.exception.BusinessException;
import com.bank.api.repository.AccountRepository;
import com.bank.api.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserService userService;

    public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    @Transactional
    public TransactionResponseDTO transfer(TransactionRequestDTO request) {
        // Find accounts
        Account sourceAccount = accountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new BusinessException("Source account not found"));
        
        Account destinationAccount = accountRepository.findByAccountNumber(request.getDestinationAccountNumber())
                .orElseThrow(() -> new BusinessException("Destination account not found"));

        // Validate balance
        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("Insufficient balance");
        }

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setDescription(request.getDescription());

        try {
            // Update balances
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
            destinationAccount.setBalance(destinationAccount.getBalance().add(request.getAmount()));

            // Save accounts
            accountRepository.save(sourceAccount);
            accountRepository.save(destinationAccount);

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

    public List<TransactionResponseDTO> getTransactionsByUserId(Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new BusinessException("User not found");
        }
        
        Account account = user.getAccount();
        if (account == null) {
            throw new BusinessException("User has no associated account");
        }
        
        List<Transaction> transactions = transactionRepository.findAllTransactionsByAccount(account);
        
        return transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(transaction.getId());
        response.setSourceAccountNumber(transaction.getSourceAccount().getAccountNumber());
        response.setDestinationAccountNumber(transaction.getDestinationAccount().getAccountNumber());
        response.setAmount(transaction.getAmount());
        response.setStatus(transaction.getStatus());
        response.setType(transaction.getType());
        response.setDescription(transaction.getDescription());
        response.setCreatedAt(transaction.getCreatedAt());
        return response;
    }
} 