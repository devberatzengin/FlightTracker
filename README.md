# Flight Tracker System (Backend) ️🛫

Welcome to the backend architecture of the **Flight Tracker System**! This repository hosts a robust, scalable, and SOLID-compliant API designed for flight tracking, booking management, and SkyWallet financial operations.

## 🚀 Key Features

- **✈️ Flight Management**: Real-time flight tracking, occupancy-based dynamic pricing, and captain assignments.
- **💳 SkyWallet**: Integrated digital wallet for secure ticket purchases and fund management.
- **🛡️ Secure Auth**: JWT-based stateless authentication with role-based access control (RBAC). 
- **🏷️ Smart Booking**: Automatic PNR generation and real-time seat map management.
- **🔔 Notification System**: Automated notifications for flight status updates and booking confirmations.
- **🌤️ Weather Integration**: Interface-driven weather data for flight planning.

## 🛠️ Technology Stack

- **Core**: Java 17+, Spring Boot 3.x
- **Security**: Spring Security, JWT (JJWT)
- **Data**: Spring Data JPA, Hibernate, PostgreSQL
- **Tooling**: Maven, Lombok, Jakarta Validation
- **Architectural Patterns**: SOLID Principles, Rich Domain Models (DDD), Modular Services.

## 🏗️ Architecture Overview

The project underwent a significant architectural upgrade to ensure modularity and scalability:
- **Rich Domain Model**: Business logic is encapsulated within Entities (`User.chargeBalance()`, `Flight.incrementOccupancy()`), adhering to the "Tell, Don't Ask" principle.
- **Interface Segregation**: Specialized services (`IPricingService`, `IPnrService`, `ISecurityService`) handle cross-cutting concerns.
- **Global Error Handling**: Centralized exception management with detailed causal diagnostics.
- **Thin Controllers**: Controllers focus solely on HTTP orchestration, delegating all logic to the service layer.

## 🏁 Getting Started

### Prerequisites
- JDK 17 or higher.
- Maven.
- PostgreSQL (or any JPA-compatible database).

### Configuration
Update the `src/main/resources/application.properties` with your credentials:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/flight_tracker
spring.datasource.username=your_username
spring.datasource.password=your_password

security.jwt.secret-key=your_secret_64_bit_key
security.jwt.expiration=86400000 # 24 hours
```

### Running the App
```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

## 📄 Documentation
- [API Documentation](API_DOCUMENTATION.md): Detailed endpoint listings and sample payloads.
- [Postman Collection](FlightTracker_Postman_Collection.json): Importable collection for quick testing.

---
Developed by **Berat Zengin** with a focus on clean code and software excellence. 🚀
