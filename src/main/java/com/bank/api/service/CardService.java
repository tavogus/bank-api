package com.bank.api.service;

import com.bank.api.dto.CardDTO;
import com.bank.api.entity.Card;
import com.bank.api.entity.User;
import com.bank.api.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final UserService userService;

    public CardService(CardRepository cardRepository, UserService userService) {
        this.cardRepository = cardRepository;
        this.userService = userService;
    }

    @Transactional
    public CardDTO createCard(Long userId, String cardHolderName) {
        User user = userService.getUserById(userId);

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

    public List<CardDTO> getCardsByUserId(Long userId) {
        List<Card> cards = cardRepository.findByUserId(userId);
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