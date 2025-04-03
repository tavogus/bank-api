package com.bank.api.repository;

import com.bank.api.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByCardNumber(String cardNumber);
    List<Card> findByUserId(Long userId);
    boolean existsByCardNumber(String cardNumber);
} 