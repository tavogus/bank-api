package com.bank.api.controller;

import com.bank.api.dto.CardCreationDTO;
import com.bank.api.dto.CardDTO;
import com.bank.api.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<CardDTO> createCard(
            @PathVariable Long userId,
            @RequestBody CardCreationDTO creationDTO) {
        return ResponseEntity.ok(cardService.createCard(userId, creationDTO.cardHolderName()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CardDTO>> getCardsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(cardService.getCardsByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDTO> getCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getCardById(id));
    }
} 