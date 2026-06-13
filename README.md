# Secure Authentication API with Spring Boot and Keycloak

This project is a Spring Boot REST API secured with Keycloak using JWT authentication and role-based access control.

## Features

* Public endpoint accessible without authentication
* Protected endpoint accessible only with a valid JWT token
* Admin endpoint accessible only by users with the `ADMIN` role
* Keycloak integration as an identity provider
* JWT validation using Spring Security OAuth2 Resource Server
* Role-based authorization using Keycloak realm roles

## Technologies Used

* Java
* Spring Boot
* Spring Security
* OAuth2 Resource Server
* Keycloak
* JWT
* Docker
* Maven

## Architecture

Keycloak is used to manage users, roles, authentication, and token generation.

Spring Boot acts as a Resource Server. It does not handle login directly. Instead, it validates JWT access tokens issued by Keycloak.

```text
User → Login with Keycloak → Receives JWT token
User → Sends JWT token to Spring Boot API
Spring Boot → Validates token and checks user role
```

## Keycloak Configuration

Keycloak runs on:

```text
http://localhost:8180
```

Realm:

```text
spring-auth-realm
```

Client:

```text
spring-auth-client
```

Roles:

```text
USER
ADMIN
```

Users:

```text
user1 / user123 / USER
admin1 / admin123 / ADMIN
```

The client must have:

```text
Direct access grants: ON
Standard flow: ON
Client authentication: OFF
```

## Application Configuration

The Spring Boot application runs on:

```text
http://localhost:8081
```

The JWT issuer is configured in `application.properties`:

```properties
server.port=8081

spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8180/realms/spring-auth-realm
```

## API Endpoints

### Public endpoint

```http
GET /api/public
```

Accessible without token.

Expected response:

```json
{
  "message": "This endpoint is public"
}
```

### User endpoint

```http
GET /api/user
```

Requires a valid JWT token with role `USER` or `ADMIN`.

Expected response:

```json
{
  "message": "Hello authenticated user",
  "username": "user1",
  "roles": ["USER"]
}
```

### Admin endpoint

```http
GET /api/admin
```

Requires a valid JWT token with role `ADMIN`.

Expected response:

```json
{
  "message": "Hello admin",
  "username": "admin1",
  "roles": ["ADMIN"]
}
```

## Run Keycloak with Docker

```bash
docker run --name keycloak-auth -p 8180:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:latest start-dev
```

If the container already exists:

```bash
docker start keycloak-auth
```

## Run the Spring Boot Application

Using Maven:

```bash
mvn spring-boot:run
```

Or run the main class from IntelliJ.

## Get Access Token for USER

```powershell
$response = curl.exe -s -X POST "http://localhost:8180/realms/spring-auth-realm/protocol/openid-connect/token" -H "Content-Type: application/x-www-form-urlencoded" -d "grant_type=password" -d "client_id=spring-auth-client" -d "username=user1" -d "password=user123" | ConvertFrom-Json

$token = $response.access_token
```

Test user endpoint:

```powershell
curl.exe -i -H "Authorization: Bearer $token" http://localhost:8081/api/user
```

Test admin endpoint with USER token:

```powershell
curl.exe -i -H "Authorization: Bearer $token" http://localhost:8081/api/admin
```

Expected result:

```text
HTTP/1.1 403
```

## Get Access Token for ADMIN

```powershell
$responseAdmin = curl.exe -s -X POST "http://localhost:8180/realms/spring-auth-realm/protocol/openid-connect/token" -H "Content-Type: application/x-www-form-urlencoded" -d "grant_type=password" -d "client_id=spring-auth-client" -d "username=admin1" -d "password=admin123" | ConvertFrom-Json

$adminToken = $responseAdmin.access_token
```

Test admin endpoint:

```powershell
curl.exe -i -H "Authorization: Bearer $adminToken" http://localhost:8081/api/admin
```

Expected result:

```text
HTTP/1.1 200
```

## Security Behavior

| Case                                 | Result           |
| ------------------------------------ | ---------------- |
| Access `/api/public` without token   | 200 OK           |
| Access `/api/user` without token     | 401 Unauthorized |
| Access `/api/user` with USER token   | 200 OK           |
| Access `/api/admin` with USER token  | 403 Forbidden    |
| Access `/api/admin` with ADMIN token | 200 OK           |

