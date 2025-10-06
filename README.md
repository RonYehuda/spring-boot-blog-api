# Spring Boot Blog API

A learning project demonstrating Spring Boot REST API development with comprehensive features.

## Features

- **User Management**: CRUD operations for users
- **Blog Posts**: Create and manage posts linked to users
- **JWT Authentication**: Secure login and token-based authorization
- **Role-Based Access**: USER and ADMIN roles
- **Input Validation**: Using Bean Validation annotations
- **Exception Handling**: Global error handling with custom responses
- **Comprehensive Testing**: Unit tests and integration tests with MockMvc

## Technologies

- Spring Boot 3.x
- Spring Security with JWT
- Spring Data JPA
- MySQL (production)
- H2 Database (testing)
- Maven
- JUnit 5

## Setup

1. Clone the repository
```bash
git clone https://github.com/RonYehuda/spring-boot-blog-api.git
```

2. Configure MySQL database
    - Copy `src/main/resources/application.properties.example` to `application.properties`
    - Update database credentials

3. Run the application
```bash
mvn spring-boot:run
```

## API Endpoints

### Authentication
- `POST /auth/register` - Register new user
- `POST /auth/login` - Login and receive JWT token

### Users (requires JWT token)
- `GET /users` - Get all users (ADMIN only)
- `GET /users/{id}` - Get user by ID
- `POST /users` - Create new user
- `PUT /users/{id}` - Update user
- `DELETE /users/{id}` - Delete user

### Posts (requires JWT token)
- `GET /posts` - Get all posts
- `GET /posts/user/{userId}` - Get posts by user
- `POST /users/{userId}/posts` - Create new post

## Testing

Run tests with:
```bash
mvn test
```

## Learning Goals

This project was created as a hands-on learning experience for:
- Building RESTful APIs with Spring Boot
- Implementing JWT authentication
- Writing comprehensive tests
- Database relationships (One-to-Many)
- Input validation and error handling