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
} 