package com.bank.api.controller;

import java.util.List;

import com.bank.api.dto.TransactionResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.api.dto.CardCreationDTO;
import com.bank.api.dto.CardDTO;
import com.bank.api.dto.CardPurchaseDTO;
import com.bank.api.service.CardService;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardDTO> createCard(
            @RequestBody CardCreationDTO creationDTO) {
        return ResponseEntity.ok(cardService.createCard(creationDTO.cardHolderName()));
    }

    @GetMapping("/user")
    public ResponseEntity<List<CardDTO>> getCardsByUserId() {
        return ResponseEntity.ok(cardService.getCardsByUserId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDTO> getCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getCardById(id));
    }
    
    @PostMapping("/purchase")
    public ResponseEntity<TransactionResponseDTO> purchaseWithCard(@RequestBody CardPurchaseDTO purchaseDTO) {
        return ResponseEntity.ok(cardService.processCardPurchase(purchaseDTO));
    }
} 