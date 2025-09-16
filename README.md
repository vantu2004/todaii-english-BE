# Todaii English

**Todaii English** is a multi-module project built with **Spring Boot 3** and **Java 21**, following a **Maven multi-module structure**.

### Project Goals
- Provide REST APIs for **end-users** (User API).
- Provide management APIs for **administrators** (Admin API).
- Separate concerns into **domain logic (core)**, **infrastructure (infra)**, and **shared utilities**.
- Designed to be maintainable, extensible, and ready for a future microservices architecture.

---

## 🛠️ Technology Stack

- **Java 21**
- **Spring Boot 3.3.x**
  - Spring Web (REST API)
  - Spring Validation
  - Spring Boot Test
  - Spring Boot DevTools (hot reload)
- **Maven** (multi-module project structure)
- **JPA / Hibernate** (ORM & DB mapping)
- **MySQL 8** (primary database)
- **Lombok** (reduce boilerplate code) *(recommended)*
- **JUnit 5** (unit & integration testing)
- **Docker** *(for containerization & deployment)*
- **Git/GitHub** (version control)
- **.env** + Spring Profiles (for environment-specific configuration)

---

## 📂 Project Structure

```text
todaii-english/
│── pom.xml                # Root pom: version/dependency/plugin management
│── .gitignore             # Git ignore rules
│── README.md              # Project documentation
│── .env.example           # Environment variables example file
│
├── apps                   # Executable Spring Boot applications
│   ├── pom.xml            # Parent POM for all apps
│   ├── user-api           # End-user API
│   │   ├── pom.xml
│   │   └── src/main/java/com/todaii/english/user/...
│   └── admin-api          # Admin API
│       ├── pom.xml
│       └── src/main/java/com/todaii/english/admin/...
│
├── core                   # Business logic (services, domain models)
│   ├── pom.xml
│   └── src/main/java/com/todaii/english/core/...
│
├── infra                  # Infrastructure layer (security, configurations)
│   ├── pom.xml
│   └── src/main/java/com/todaii/english/infra/...
│
└── shared                 # Shared components (common utils, ApiResponse, constants)
    ├── pom.xml
    └── src/main/java/com/todaii/english/shared/...
