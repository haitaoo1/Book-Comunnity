# Backend


The backend of this project is build using Spring Boot 3.
It includes user registration, authentication, email Validation, role-based authorization, and book management features.

## Features
- **User registration**: Securely register new users with encrypted passwords.
- **Login with JWT authentication**: Implement login functionality with JWT tokens for stateless authentication.
- **Email Validation**: Accounts need to be activated using email validation codes. (Usin maildev por developing but it may change to SMTP and Java Mail Sender)
- **Refresh token**: Generate and manage refresh tokens for maintaining user sessions.
- **Password Encryption**: Use BCrypt to securely encrypt user passwords.
- **Role-Based Authorization**: Control access to endpoints based on user roles using Spring Security.
- **Logout mechanism**: Implement a secure logout process to invalidate tokens.


## Technologies
- Spring Boot 3.0
- Spring Security
- JSON Web Tokens (JWT)
- Maven

## Getting Started
&nbsp; 1. Clone the repository

```bash
git clone https://github.com/haitaoo1/Book-Comunnity
```
&nbsp;2. Open docker-desktop(windows) and run the docker-compose file
```bash
 docker compose up -d --build
```
&nbsp;3. Navigate to Backend directory,install dependencies and run the application(.jar file)

 ```bash
   cd Backend
   mvn clean install
   java -jar taret/xxxx.jar
 
```