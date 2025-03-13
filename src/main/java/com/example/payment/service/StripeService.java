package com.example.payment.service;

import com.example.payment.model.Payment;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeService {
    @Value("${payment.stripe.api-key}")
    private String apiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

    public String processPayment(Payment payment) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(payment.getAmount().multiply(new BigDecimal("100")).longValue())
                .setCurrency(payment.getCurrency().toLowerCase())
                .setDescription(payment.getDescription())
                .putMetadata("paymentId", payment.getId().toString())
                .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            return paymentIntent.getId();
        } catch (StripeException e) {
            log.error("Stripe payment processing failed", e);
            throw new PaymentProcessingException("Stripe payment failed: " + e.getMessage());
        }
    }

    public void refundPayment(Payment payment) {
        try {
            RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(payment.getExternalReference())
                .build();

            Refund.create(params);
        } catch (StripeException e) {
            log.error("Stripe refund processing failed", e);
            throw new PaymentProcessingException("Stripe refund failed: " + e.getMessage());
        }
    }

    public void handleWebhook(String payload) {
        // Implement webhook handling logic
        log.info("Processing Stripe webhook");
    }
}