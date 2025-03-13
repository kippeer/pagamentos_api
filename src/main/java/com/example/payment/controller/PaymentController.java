package com.example.payment.controller;

import com.example.payment.dto.CreatePaymentRequest;
import com.example.payment.dto.PaymentDTO;
import com.example.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment management endpoints")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create a new payment")
    public ResponseEntity<PaymentDTO> createPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreatePaymentRequest request) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        PaymentDTO payment = paymentService.createPayment(userId, request);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<PaymentDTO> getPayment(@PathVariable UUID id) {
        PaymentDTO payment = paymentService.getPayment(id);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/user")
    @Operation(summary = "Get all payments for the authenticated user")
    public ResponseEntity<List<PaymentDTO>> getUserPayments(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        List<PaymentDTO> payments = paymentService.getUserPayments(userId);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "Refund a payment")
    public ResponseEntity<PaymentDTO> refundPayment(@PathVariable UUID id) {
        PaymentDTO payment = paymentService.refundPayment(id);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/webhook")
    @Operation(summary = "Handle payment provider webhooks")
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader("X-Payment-Provider") String provider,
            @RequestBody String payload) {
        switch (provider.toLowerCase()) {
            case "stripe" -> stripeService.handleWebhook(payload);
            case "paypal" -> payPalService.handleWebhook(payload);
            case "pix" -> pixService.handleWebhook(payload);
            default -> throw new IllegalArgumentException("Unknown payment provider: " + provider);
        }
        return ResponseEntity.ok().build();
    }
}