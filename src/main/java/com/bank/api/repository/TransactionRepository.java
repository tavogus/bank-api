package com.bank.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.api.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
} 