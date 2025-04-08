package com.bank.api.controller;

import com.bank.api.dto.InvoiceDTO;
import com.bank.api.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<InvoiceDTO>> getCardInvoices(@PathVariable Long cardId) {
        return ResponseEntity.ok(invoiceService.getCardInvoices(cardId));
    }

    @PostMapping("/{invoiceId}/pay")
    public ResponseEntity<Void> payInvoice(@PathVariable Long invoiceId) {
        invoiceService.payInvoice(invoiceId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{invoiceId}/close")
    public ResponseEntity<Void> closeInvoice(@PathVariable Long invoiceId) {
        invoiceService.closeInvoice(invoiceId);
        return ResponseEntity.ok().build();
    }
} 