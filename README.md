# Payment Service API

A robust payment processing service built with Spring Boot that handles multiple payment methods including Stripe, PayPal, and PIX.

## Features

### Payment Processing
- Multiple payment methods support:
  - Credit Card (Stripe)
  - PayPal
  - PIX (Brazilian instant payments)
- Secure payment handling
- Transaction status tracking
- Refund processing
- Webhook handling for real-time updates

### Security
- JWT-based authentication
- Role-based access control
- Secure password hashing
- API key validation
- CORS configuration

### Audit & Monitoring
- Comprehensive audit logging
- Transaction tracking
- User activity monitoring
- Payment status tracking

## Tech Stack

- Java 17
- Spring Boot 3.2.3
- PostgreSQL
- Redis (caching)
- Stripe API
- PayPal API
- JWT for authentication

## Prerequisites

- JDK 17
- Docker and Docker Compose
- PostgreSQL
- Redis
- Maven

## Environment Setup

1. Clone the repository
2. Copy `.env.example` to `.env` and configure:

```env
DB_USERNAME=postgres
DB_PASSWORD=adm
REDIS_HOST=localhost
REDIS_PORT=6379

# Stripe Configuration
STRIPE_API_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...

# PayPal Configuration
PAYPAL_CLIENT_ID=your_client_id
PAYPAL_CLIENT_SECRET=your_client_secret
PAYPAL_MODE=sandbox

# PIX Configuration
PIX_API_KEY=your_pix_api_key
PIX_ENDPOINT=https://api.pix.example.com

# JWT Configuration
JWT_SECRET=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
```

## Quick Start

1. Start dependencies:
```bash
docker-compose up -d
```

2. Build the application:
```bash
./mvnw clean package
```

3. Run the application:
```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

## API Documentation

### Authentication

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "secure_password"
}
```

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "secure_password"
}
```

### Payments

```http
POST /api/v1/payments
Authorization: Bearer <token>
Content-Type: application/json

{
  "amount": 100.00,
  "currency": "USD",
  "paymentMethod": "CREDIT_CARD",
  "description": "Product purchase"
}
```

```http
GET /api/v1/payments/{id}
Authorization: Bearer <token>
```

```http
POST /api/v1/payments/{id}/refund
Authorization: Bearer <token>
```

```http
GET /api/v1/payments/user
Authorization: Bearer <token>
```

### Webhooks

```http
POST /api/v1/payments/webhook
X-Payment-Provider: stripe
```

```http
POST /api/v1/payments/webhook
X-Payment-Provider: paypal
```

```http
POST /api/v1/payments/webhook
X-Payment-Provider: pix
```

## Security

### JWT Authentication
- All endpoints except `/api/v1/auth/**` and webhooks require JWT authentication
- Tokens expire after 24 hours
- Include the token in the Authorization header: `Bearer <token>`

### Data Protection
- Passwords are hashed using BCrypt
- Sensitive data is encrypted
- TLS for data in transit

## Error Handling

The API uses standard HTTP status codes and returns detailed error messages:

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2025-03-12T18:30:00Z",
  "errors": {
    "amount": "must be greater than 0"
  }
}
```

Common status codes:
- 200: Success
- 201: Created
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 500: Internal Server Error

## Monitoring

The application exposes several actuator endpoints for monitoring:

- `/actuator/health`: System health information
- `/actuator/metrics`: Application metrics
- `/actuator/prometheus`: Prometheus metrics

## Development

### Code Style
- Follow Google Java Style Guide
- Use meaningful variable and method names
- Add comments for complex logic
- Include JavaDoc for public methods

### Testing
- Unit tests with JUnit 5
- Integration tests with TestContainers
- API tests with Spring MockMvc
- Coverage report with JaCoCo

### Database Migrations
- Managed with Flyway
- Located in `src/main/resources/db/migration`
- Version controlled and repeatable

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.