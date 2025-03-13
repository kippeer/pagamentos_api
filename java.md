# Payment Service

A robust payment processing service built with Spring Boot that handles multiple payment methods including Stripe, PayPal, and Pix.

## Project Structure

```
payment-service/
│── src/
│   ├── main/
│   │   ├── java/com/example/payment/
│   │   │   ├── config/         # General configurations (Beans, CORS, Security)
│   │   │   ├── controller/     # REST endpoints (Stripe, PayPal, Pix)
│   │   │   ├── dto/           # DTOs for requests and responses
│   │   │   ├── model/         # Database entities (Payments, Invoices, Subscriptions)
│   │   │   ├── repository/    # Database access interfaces
│   │   │   ├── service/       # Business logic (Stripe, PayPal, and Pix integration)
│   │   │   ├── util/          # Helpers for conversions and validations
│   │   ├── resources/
│   │   │   ├── application.yml # Spring Boot configurations
│── test/                      # Unit and integration tests
│── Dockerfile                 # Docker containerization
│── docker-compose.yml         # Database and dependencies setup
│── pom.xml                    # Spring Boot and payment lib dependencies
│── README.md                  # Project documentation
```

## Core Features

### 1. One-Time Payments
- **Payment Processing**
  - Integration with Stripe API for credit card payments
  - PayPal integration for PayPal wallet payments
  - Pix integration for instant Brazilian payments
  - Secure payment processing with encryption
  - Transaction status tracking

- **Webhook Processing**
  - Automated payment confirmation handling
  - Real-time status updates
  - Retry mechanism for failed webhooks

- **Refunds and Cancellations**
  - Full and partial refund support
  - Automated refund processing
  - Cancellation audit logging

### 2. Subscription Management
- **Subscription Lifecycle**
  - Subscription creation with different billing cycles
  - Automatic renewal processing
  - Grace period handling
  - Subscription cancellation workflow

- **Plans and Pricing**
  - Support for multiple subscription plans
    - Monthly plans
    - Quarterly plans
    - Annual plans
  - Flexible pricing tiers
  - Proration handling for plan changes

- **Notifications**
  - Pre-renewal notifications
  - Payment failure alerts
  - Subscription status updates

### 3. Billing and Receipts
- **Invoice Generation**
  - Automated invoice creation
  - Custom invoice numbering
  - Tax calculation and handling
  - Multiple currency support

- **Receipt Management**
  - Automated receipt generation
  - Email delivery system
  - PDF generation and storage

- **Financial Dashboard**
  - Transaction monitoring
  - Revenue analytics
  - Payment status tracking
  - Refund monitoring

### 4. Webhooks and Events
- **Event Processing**
  - Asynchronous payment confirmation
  - Event queueing and retry mechanism
  - Event logging and monitoring

- **Audit System**
  - Detailed transaction logs
  - Financial audit trail
  - Security event logging

- **Notification System**
  - Email notifications
  - SMS alerts (optional)
  - In-app notifications

## Technical Stack

### Backend Framework
- Spring Boot 3.x
- Java 17
- Spring Security
- Spring Data JPA

### Database
- PostgreSQL
- Redis (for caching)

### Payment Integrations
- Stripe API
- PayPal REST API
- Pix (Brazilian instant payments)

### Testing
- JUnit 5
- Mockito
- TestContainers

### Documentation
- OpenAPI (Swagger)
- JavaDoc

### Monitoring
- Spring Actuator
- Prometheus
- Grafana

## Security Measures

1. **Authentication**
   - JWT-based authentication
   - Role-based access control
   - API key validation

2. **Data Protection**
   - PCI DSS compliance
   - Encryption at rest
   - TLS for data in transit

3. **Audit**
   - Transaction logging
   - Access logging
   - Error tracking

## Development Setup

### Prerequisites
- JDK 17
- Maven
- Docker
- PostgreSQL
- Redis

### Configuration
```yaml
# application.yml example
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/payment_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

payment:
  stripe:
    apiKey: ${STRIPE_API_KEY}
    webhookSecret: ${STRIPE_WEBHOOK_SECRET}
  paypal:
    clientId: ${PAYPAL_CLIENT_ID}
    clientSecret: ${PAYPAL_CLIENT_SECRET}
  pix:
    apiKey: ${PIX_API_KEY}
```

### Running Locally
```bash
# Start dependencies
docker-compose up -d

# Run application
./mvnw spring-boot:run
```

## API Documentation

### Payment Endpoints

```http
POST /api/v1/payments/stripe
POST /api/v1/payments/paypal
POST /api/v1/payments/pix
GET /api/v1/payments/{id}
POST /api/v1/payments/{id}/refund
```

### Subscription Endpoints

```http
POST /api/v1/subscriptions
GET /api/v1/subscriptions/{id}
PUT /api/v1/subscriptions/{id}
DELETE /api/v1/subscriptions/{id}
```

### Webhook Endpoints

```http
POST /api/v1/webhooks/stripe
POST /api/v1/webhooks/paypal
POST /api/v1/webhooks/pix
```

## Deployment

### Docker Build
```dockerfile
FROM eclipse-temurin:17-jdk-alpine
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Kubernetes Configuration
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: payment-service
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: payment-service
        image: payment-service:latest
        ports:
        - containerPort: 8080
```

## Monitoring and Metrics

- Transaction success rate
- Payment processing time
- Error rates
- API response times
- Webhook processing status
- System resource utilization

## Future Enhancements

1. **Additional Payment Methods**
   - Apple Pay integration
   - Google Pay support
   - Cryptocurrency payments

2. **Advanced Analytics**
   - Revenue forecasting
   - Churn prediction
   - Customer payment patterns

3. **Enhanced Security**
   - 3D Secure 2.0
   - Fraud detection system
   - Enhanced encryption

4. **Performance Optimization**
   - Caching improvements
   - Database optimization
   - API response time reduction