package com.bank.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bank.api.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findByUserId(Long userId);
    boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT a FROM Account a WHERE a.user.email = :username")
    Optional<Account> findByUserUsername(String username);
} 