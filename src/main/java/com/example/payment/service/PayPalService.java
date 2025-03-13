package com.example.payment.service;

import com.example.payment.model.Payment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayPalService {
    @Value("${payment.paypal.client-id}")
    private String clientId;

    @Value("${payment.paypal.client-secret}")
    private String clientSecret;

    private final PayPalHttpClient payPalClient;

    public String processPayment(Payment payment) {
        try {
            OrdersCreateRequest request = new OrdersCreateRequest();
            request.prefer("return=representation");
            request.requestBody(createOrderRequest(payment));

            HttpResponse<Order> response = payPalClient.execute(request);
            Order order = response.result();
            
            return order.id();
        } catch (IOException e) {
            log.error("PayPal payment processing failed", e);
            throw new PaymentProcessingException("PayPal payment failed: " + e.getMessage());
        }
    }

    public void refundPayment(Payment payment) {
        try {
            // Implement PayPal refund logic
            log.info("Processing PayPal refund for payment: {}", payment.getId());
        } catch (Exception e) {
            log.error("PayPal refund processing failed", e);
            throw new PaymentProcessingException("PayPal refund failed: " + e.getMessage());
        }
    }

    public void handleWebhook(String payload) {
        // Implement webhook handling logic
        log.info("Processing PayPal webhook");
    }

    private OrderRequest createOrderRequest(Payment payment) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");
        
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
            .amountWithBreakdown(new AmountWithBreakdown().currencyCode(payment.getCurrency())
                .value(payment.getAmount().toString()));
        
        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));
        return orderRequest;
    }
}