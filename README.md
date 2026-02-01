# PayFlow – Production-Ready E-Commerce Checkout System

PayFlow is a **full-stack e-commerce checkout system** designed with a **Java-first backend architecture** and a modern React frontend.

It demonstrates real-world backend engineering concepts including **transactional inventory control**, **secure payment processing**, **JWT authentication**, **concurrency handling**, and **containerized deployment**.

This project is built to reflect **production-grade design decisions**, not just a demo application.

---

## 🧱 Architecture Overview

React (TypeScript, Vite)
       
        ↓
REST API (Spring Boot, Java 21)

        ↓
PostgreSQL (Flyway migrations)

        ↓
Stripe Checkout + Webhooks


### Technology Stack

- **Frontend**: React + TypeScript (Vite)
- **Backend**: Spring Boot (Java 21)
- **Database**: PostgreSQL
- **Payments**: Stripe Checkout + signed webhooks
- **Authentication**: JWT (stateless)
- **Infrastructure**: Docker + Docker Compose

---

## ✨ Key Features

### 🔐 Authentication & Security
- JWT-based stateless authentication  
- Secure password hashing with BCrypt  
- Role-ready Spring Security configuration  
- Centralized exception handling  
- Input validation using Jakarta Validation  
- Rate limiting filter to prevent abuse  

### 🛒 Checkout & Inventory Management
- Transactional checkout flow  
- Pessimistic row-level locking to prevent overselling  
- Inventory reservation with expiration window  
- Safe handling of concurrent checkouts  
- Idempotency safeguards for payment flow  

### 💳 Payments
- Stripe Checkout Sessions  
- Secure Stripe webhook verification  
- Order lifecycle management (`PENDING → PAID`)  
- Payment success and failure handling  

### 🗄️ Backend Engineering
- Layered architecture (Controller / Service / Repository)  
- Flyway-managed database migrations  
- Clean DTO separation  
- Dockerized Spring Boot runtime  
- Production-style error responses  

---

## 📁 Project Structure

payflow/

├── backend/

│   ├── src/main/java/com/payflow/

│   │   ├── controller/

│   │   ├── service/

│   │   ├── repository/

│   │   ├── entity/

│   │   ├── dto/

│   │   ├── config/

│   │   └── util/

│   └── resources/

│       ├── application.yml

│       └── db/migration/

├── frontend/

│   ├── src/

│   │   ├── components/

│   │   ├── pages/

│   │   ├── store/

│   │   └── api/

│   └── vite.config.ts

├── docker-compose.yml

└── README.md


---

## 🚀 Running the Project

### Prerequisites
- Docker + Docker Compose  
- Node.js 18+  
- Java 21 (for local backend development)  
- Stripe account (test keys)

---

## 🚀 Running the Project

### Prerequisites
- Docker + Docker Compose
- Node.js 18+
- Java 21 (for local backend development)
- Stripe account (test keys)

---

### 1️⃣ Environment Setup

Create a `.env` file in the project root and add the following values:

```env
JWT_SECRET=replace_with_long_random_secret
STRIPE_SECRET_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...
```
### 2️⃣ Run with Docker (Recommended)

Build and start all services using Docker Compose:

```bash
docker compose up --build
```

### Once running, the services will be available at:

Frontend → http://localhost:5173

Backend → http://localhost:8080

PostgreSQL → localhost:5432


3️⃣ Stripe Webhooks (Local Development)

To handle Stripe webhook events locally, install the Stripe CLI and run:

stripe login

stripe listen --forward-to localhost:8080/api/webhooks/stripe


Copy the generated whsec_... value and update it as STRIPE_WEBHOOK_SECRET in your .env file.


🔄 API Overview

Authentication

POST /api/auth/register

POST /api/auth/login


Products

GET  /api/products

POST /api/products


Checkout

POST /api/checkout/session


WebHooks

POST /api/webhooks/stripe



🧠 Engineering Highlights

Uses pessimistic locking to ensure inventory correctness under concurrent checkouts

Prevents duplicate processing using idempotency patterns

Clean separation between domain entities and API DTOs

Designed with scalability and reliability in mind

Backend logic intentionally mirrors real production systems


📌 Why This Project Matters

This project demonstrates:

Strong Java backend engineering fundamentals

Hands-on experience with payment systems (Stripe)

Understanding of distributed system edge cases

Production-oriented backend architecture

End-to-end ownership from frontend UI to database and infrastructure

👩‍💻 Author

Shreya Padaganur

Full-Stack Software Engineer
