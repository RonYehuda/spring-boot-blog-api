# Spring Boot Blog API

A production-ready RESTful API for a blogging platform, featuring JWT authentication, role-based access control, and full user & post management.

## Features

- **JWT Authentication** — Secure login with token-based authorization
- **Role-Based Access Control** — USER and ADMIN roles with protected routes
- **User Management** — Full CRUD operations
- **Blog Posts** — Create and manage posts linked to users
- **Input Validation** — Bean Validation with meaningful error responses
- **Global Exception Handling** — Consistent error format across all endpoints
- **Comprehensive Testing** — Unit tests and integration tests with MockMvc
- **API Documentation** — Interactive Swagger/OpenAPI docs

## Tech Stack

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

- **Spring Boot 3.x** + Spring Security
- **Spring Data JPA** / Hibernate
- **MySQL** (production) / H2 (testing)
- **Maven**
- **JUnit 5** + MockMvc

## Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/RonYehuda/spring-boot-blog-api.git
cd spring-boot-blog-api
```

### 2. Configure the database
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
# Update DB credentials in application.properties
```

### 3. Run the application
```bash
mvn spring-boot:run
```

### 4. Explore the API
Open Swagger UI at: `http://localhost:8080/swagger-ui.html`

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Login and receive JWT token |

### Users *(requires JWT)*
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/users` | Get all users (ADMIN only) |
| GET | `/users/{id}` | Get user by ID |
| POST | `/users` | Create new user |
| PUT | `/users/{id}` | Update user |
| DELETE | `/users/{id}` | Delete user |

### Posts *(requires JWT)*
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/posts` | Get all posts |
| GET | `/posts/user/{userId}` | Get posts by user |
| POST | `/users/{userId}/posts` | Create new post |

## Running Tests
```bash
mvn test
```

## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/example/blog/
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── model/
│   │       ├── dto/
│   │       ├── security/       # JWT filter, config
│   │       └── exception/      # Global error handling
│   └── resources/
└── test/
```
