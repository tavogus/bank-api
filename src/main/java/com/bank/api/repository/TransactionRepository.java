package com.bank.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bank.api.entity.Account;
import com.bank.api.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceAccount(Account account);
    List<Transaction> findByDestinationAccount(Account account);
    
    @Query("SELECT t FROM Transaction t WHERE t.sourceAccount = :account OR t.destinationAccount = :account ORDER BY t.createdAt DESC")
    List<Transaction> findAllTransactionsByAccount(@Param("account") Account account);
} 