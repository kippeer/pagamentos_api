package com.example.payment.service;

import com.example.payment.dto.CreatePaymentRequest;
import com.example.payment.dto.PaymentDTO;
import com.example.payment.model.Payment;
import com.example.payment.model.PaymentStatus;
import com.example.payment.model.User;
import com.example.payment.repository.PaymentRepository;
import com.example.payment.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final StripeService stripeService;
    private final PayPalService payPalService;
    private final PixService pixService;

    @Transactional
    public PaymentDTO createPayment(UUID userId, CreatePaymentRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setDescription(request.getDescription());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setUser(user);

        Payment savedPayment = paymentRepository.save(payment);

        // Process payment based on method
        try {
            String externalReference = switch (request.getPaymentMethod()) {
                case CREDIT_CARD -> stripeService.processPayment(savedPayment);
                case PAYPAL -> payPalService.processPayment(savedPayment);
                case PIX -> pixService.processPayment(savedPayment);
            };
            
            savedPayment.setExternalReference(externalReference);
            savedPayment.setStatus(PaymentStatus.PROCESSING);
            savedPayment = paymentRepository.save(savedPayment);
            
            return convertToDTO(savedPayment);
        } catch (Exception e) {
            log.error("Payment processing failed", e);
            savedPayment.setStatus(PaymentStatus.FAILED);
            savedPayment.setErrorMessage(e.getMessage());
            paymentRepository.save(savedPayment);
            throw new PaymentProcessingException("Payment processing failed: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "payments", key = "#id")
    public PaymentDTO getPayment(UUID id) {
        return paymentRepository.findById(id)
            .map(this::convertToDTO)
            .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getUserPayments(UUID userId) {
        return paymentRepository.findByUserId(userId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public PaymentDTO confirmPayment(String externalReference) {
        Payment payment = paymentRepository.findByExternalReference(externalReference)
            .stream()
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);

        return convertToDTO(savedPayment);
    }

    @Transactional
    public PaymentDTO refundPayment(UUID id) {
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Payment cannot be refunded");
        }

        try {
            switch (payment.getPaymentMethod()) {
                case CREDIT_CARD -> stripeService.refundPayment(payment);
                case PAYPAL -> payPalService.refundPayment(payment);
                case PIX -> pixService.refundPayment(payment);
            }

            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setRefundedAt(LocalDateTime.now());
            Payment savedPayment = paymentRepository.save(payment);

            return convertToDTO(savedPayment);
        } catch (Exception e) {
            log.error("Refund processing failed", e);
            throw new PaymentProcessingException("Refund processing failed: " + e.getMessage());
        }
    }

    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setCurrency(payment.getCurrency());
        dto.setDescription(payment.getDescription());
        dto.setExternalReference(payment.getExternalReference());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setPaidAt(payment.getPaidAt());
        dto.setCanceledAt(payment.getCanceledAt());
        dto.setRefundedAt(payment.getRefundedAt());
        dto.setErrorMessage(payment.getErrorMessage());
        return dto;
    }
}