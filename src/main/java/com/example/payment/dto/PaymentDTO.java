package com.example.payment.dto;

import com.example.payment.model.PaymentMethod;
import com.example.payment.model.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentDTO {
    private UUID id;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private String currency;
    private String description;
    private String externalReference;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime canceledAt;
    private LocalDateTime refundedAt;
    private String errorMessage;
}