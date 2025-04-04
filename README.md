# Digital Bank API

A RESTful API for a digital bank system built with Spring Boot 3.3.10 and Java 24.

## Features

- User registration and authentication with JWT
- Account management (create, deposit, withdraw)
- Card management (create, list)
- PostgreSQL database with Docker

## Prerequisites

- Java 21
- Maven
- Docker and Docker Compose
- PostgreSQL (via Docker)

## Getting Started

1. Clone the repository:
```bash
git clone <repository-url>
cd bank-api
```

2. Start the PostgreSQL database using Docker Compose:
```bash
docker-compose up -d
```

3. Build the application:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## API Endpoints

### Authentication
- POST `/api/auth/register` - Register a new user
- POST `/api/auth/login` - Login and get JWT token

### Accounts
- POST `/api/accounts/{userId}` - Create a new account
- POST `/api/accounts/{accountId}/deposit` - Deposit money
- POST `/api/accounts/{accountId}/withdraw` - Withdraw money
- GET `/api/accounts/{id}` - Get account details
- GET `/api/accounts/user/{userId}` - Get account by user ID

### Cards
- POST `/api/cards/{userId}` - Create a new card
- GET `/api/cards/user/{userId}` - Get user's cards
- GET `/api/cards/{id}` - Get card details

### Transactions
- POST `/api/transactions/transfer` - Transfer money between accounts
- GET `/api/transactions/user/{userId}` - Get user's transaction history

## Security

The API uses JWT (JSON Web Tokens) for authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-token>
```

## Database

The application uses PostgreSQL running in Docker. The database configuration is in `application.yml`:
- Database: bankdb
- Username: bankuser
- Password: bankpass
- Port: 5432 