package com.bank.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.api.dto.CardDTO;
import com.bank.api.dto.CardPurchaseDTO;
import com.bank.api.dto.TransactionResponseDTO;
import com.bank.api.entity.Account;
import com.bank.api.entity.Card;
import com.bank.api.entity.PaymentType;
import com.bank.api.entity.User;
import com.bank.api.repository.CardRepository;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final TransactionService transactionService;
    private final AccountService accountService;

    public CardService(CardRepository cardRepository, 
                      TransactionService transactionService,
                      AccountService accountService) {
        this.cardRepository = cardRepository;
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    @Transactional
    public CardDTO createCard(String cardHolderName) {
        User user = UserContextService.getCurrentUser();

        Card card = new Card();
        card.setUser(user);
        card.setCardHolderName(cardHolderName);
        card.setCardNumber(generateCardNumber());
        card.setCvv(generateCVV());
        card.setExpirationDate(LocalDateTime.now().plusYears(5));

        card = cardRepository.save(card);
        return new CardDTO(
                card.getId(),
                card.getCardNumber(),
                card.getCardHolderName(),
                card.getExpirationDate(),
                card.getUser().getId(),
                card.getCreatedAt(),
                card.getUpdatedAt()
        );
    }

    public List<CardDTO> getCardsByUserId() {
        User user = UserContextService.getCurrentUser();

        if (user == null) {
            return new ArrayList<>();
        }
        List<Card> cards = cardRepository.findByUserId(user.getId());
        return cards.stream()
                .map(card -> new CardDTO(
                        card.getId(),
                        card.getCardNumber(),
                        card.getCardHolderName(),
                        card.getExpirationDate(),
                        card.getUser().getId(),
                        card.getCreatedAt(),
                        card.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    public CardDTO getCardById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        return new CardDTO(
                card.getId(),
                card.getCardNumber(),
                card.getCardHolderName(),
                card.getExpirationDate(),
                card.getUser().getId(),
                card.getCreatedAt(),
                card.getUpdatedAt()
        );
    }

    @Transactional
    public TransactionResponseDTO processCardPurchase(CardPurchaseDTO purchaseDTO) {
        User currentUser = UserContextService.getCurrentUser();
        
        Card card = cardRepository.findById(purchaseDTO.cardId())
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (!card.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Card does not belong to the current user");
        }

        Account account = currentUser.getAccount();

        return transactionService.createCreditCardTransaction(
            account,
            card,
            purchaseDTO.amount(),
            purchaseDTO.description(),
            purchaseDTO.paymentType()
        );
    }

    private String generateCardNumber() {
        String cardNumber;
        do {
            cardNumber = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        } while (cardRepository.existsByCardNumber(cardNumber));
        return cardNumber;
    }

    private String generateCVV() {
        return String.format("%03d", (int) (Math.random() * 1000));
    }
} 