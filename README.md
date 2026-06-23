# Job Hunter API

A RESTful backend service for a job recruitment platform. The application lets companies publish job listings, candidates apply with resumes, and administrators manage users, roles, skills, and companies. It also supports job-alert subscriptions and email notifications based on skill preferences.

Built with **Spring Boot 3**, **Spring Security (JWT)**, **JPA/Hibernate**, and **MySQL**.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [Default Seed Data](#default-seed-data)
- [Authentication](#authentication)
- [Roles & Authorization](#roles--authorization)
- [API Response Format](#api-response-format)
- [Pagination & Filtering](#pagination--filtering)
- [File Upload](#file-upload)
- [Email Notifications](#email-notifications)
- [API Endpoints](#api-endpoints)
- [API Documentation (Swagger)](#api-documentation-swagger)
- [CORS Configuration](#cors-configuration)
- [Running Tests](#running-tests)

---

## Features

- **User management** — CRUD for users with role-based access (Super Admin only for admin endpoints)
- **Authentication** — JWT access tokens with HTTP-only refresh token cookies
- **Company & job management** — Companies post jobs with skills, salary, level, and deadlines
- **Resume submissions** — Candidates apply to jobs; HR can review and update application status
- **Skills catalog** — Reusable skill tags linked to jobs and subscribers
- **Role management** — Configurable roles (`SUPER_ADMIN`, `HR`, `USER`)
- **File storage** — Upload resumes and documents (images, Word files)
- **Job subscriptions** — Users subscribe to job alerts by skill; admin can trigger bulk email sends
- **Dynamic filtering & pagination** — Query list endpoints with Spring Filter syntax
- **Unified API responses** — Consistent wrapper for success and error payloads
- **OpenAPI / Swagger UI** — Interactive API documentation

---

## Tech Stack

| Category    | Technology                                            |
| ----------- | ----------------------------------------------------- |
| Language    | Java 21                                               |
| Framework   | Spring Boot 3.3.4                                     |
| Security    | Spring Security + OAuth2 Resource Server (JWT, HS512) |
| Database    | MySQL 8.0                                             |
| ORM         | Spring Data JPA / Hibernate                           |
| Mapping     | ModelMapper                                           |
| Validation  | Jakarta Bean Validation                               |
| Email       | Spring Mail + Thymeleaf templates                     |
| API Docs    | SpringDoc OpenAPI 2.5.0                               |
| Filtering   | springfilter-jpa 3.1.7                                |
| Build       | Gradle (Kotlin DSL)                                   |
| Container   | Docker Compose (MySQL)                                |
| Env loading | spring-dotenv                                         |

---

## Project Structure

```
JobHunter/
├── src/main/java/org/example/jobhunter/
│   ├── config/          # Security, CORS, OpenAPI, DB seed, static resources
│   ├── controller/      # REST controllers
│   ├── domain/          # JPA entities & DTOs
│   ├── exception/       # Custom exceptions & global handler
│   ├── repository/      # Spring Data repositories
│   ├── service/         # Business logic
│   └── util/            # JWT helpers, response formatting
├── src/main/resources/
│   ├── application.yml  # Application configuration
│   └── templates/       # Thymeleaf email templates
├── docker-compose.yml   # MySQL container
├── .env.example         # Environment variable template
└── build.gradle.kts
```

---

## Prerequisites

- **JDK 21**
- **Gradle** (wrapper included: `./gradlew` or `gradlew.bat`)
- **Docker & Docker Compose** (recommended for MySQL)
- **SMTP credentials** (optional, required for email features)

---

## Getting Started

### 1. Clone the repository

```bash
git clone <repository-url>
cd JobHunter
```

### 2. Configure environment variables

Copy the example file and fill in your values:

```bash
cp .env.example .env
```

See [Environment Variables](#environment-variables) for the full list. The application reads variables from a `.env` file via `spring-dotenv`.

> **Note:** `application.yml` expects `MYSQL_URL`, `MYSQL_USERNAME`, and `MYSQL_PASSWORD`. Make sure your `.env` uses these names (not the `SPRING_DATASOURCE_*` names shown in some older examples).

### 3. Start MySQL with Docker Compose

```bash
docker compose up -d
```

This starts a MySQL 8.0 container on port **3306** with database name `jobhunter`.

### 4. Generate a JWT secret

The JWT secret must be a **Base64-encoded** string. Example (Linux/macOS):

```bash
openssl rand -base64 64
```

Set the output as `JOBHUNTER_JWT_BASE64_SECRET` in your `.env` file.

### 5. Run the application

**Windows:**

```bash
gradlew.bat bootRun
```

**Linux/macOS:**

```bash
./gradlew bootRun
```

The server starts at **http://localhost:8080** by default.

On first startup, the app seeds default roles and a Super Admin account (see [Default Seed Data](#default-seed-data)).

---

## Environment Variables

| Variable                                          | Description                         | Example                                 |
| ------------------------------------------------- | ----------------------------------- | --------------------------------------- |
| `MYSQL_URL`                                       | JDBC connection URL                 | `jdbc:mysql://localhost:3306/jobhunter` |
| `MYSQL_USERNAME`                                  | Database username                   | `root`                                  |
| `MYSQL_PASSWORD`                                  | Database password                   | `your_password`                         |
| `MYSQL_DATABASE`                                  | Database name (Docker Compose)      | `jobhunter`                             |
| `SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE`          | Max upload file size                | `50MB`                                  |
| `SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE`       | Max multipart request size          | `50MB`                                  |
| `SPRING_DATA_WEB_PAGEABLE_ONE_INDEXED_PARAMETERS` | Use 1-based page numbers            | `true`                                  |
| `SPRING_MAIL_HOST`                                | SMTP host                           | `smtp.gmail.com`                        |
| `SPRING_MAIL_PORT`                                | SMTP port                           | `587`                                   |
| `SPRING_MAIL_USERNAME`                            | SMTP username                       | `your@gmail.com`                        |
| `SPRING_MAIL_PASSWORD`                            | SMTP password / app password        | `your_app_password`                     |
| `JOBHUNTER_JWT_BASE64_SECRET`                     | Base64-encoded JWT signing key      | _(generated)_                           |
| `JOBHUNTER_JWT_ACCESS_TOKEN_VALIDITY_IN_SECONDS`  | Access token TTL (seconds)          | `864000`                                |
| `JOBHUNTER_JWT_REFRESH_TOKEN_VALIDITY_IN_SECONDS` | Refresh token TTL (seconds)         | `864000`                                |
| `JOBHUNTER_UPLOAD_FILE_BASE_URI`                  | Local folder URI for uploaded files | `file:///D:/uploads/`                   |

**Example `.env`:**

```env
MYSQL_URL=jdbc:mysql://localhost:3306/jobhunter
MYSQL_USERNAME=root
MYSQL_PASSWORD=your_password
MYSQL_DATABASE=jobhunter

SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=50MB
SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=50MB
SPRING_DATA_WEB_PAGEABLE_ONE_INDEXED_PARAMETERS=true

SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=
SPRING_MAIL_PASSWORD=

JOBHUNTER_JWT_BASE64_SECRET=<your-base64-secret>
JOBHUNTER_JWT_ACCESS_TOKEN_VALIDITY_IN_SECONDS=864000
JOBHUNTER_JWT_REFRESH_TOKEN_VALIDITY_IN_SECONDS=864000

JOBHUNTER_UPLOAD_FILE_BASE_URI=file:///D:/uploads/
```

---

## Default Seed Data

When the database is empty, `DatabaseInitializer` creates:

### Roles

| Role          | Description                               |
| ------------- | ----------------------------------------- |
| `SUPER_ADMIN` | Full system access                        |
| `HR`          | Manage jobs and resumes for their company |
| `USER`        | Job seeker / candidate                    |

### Default admin account

| Field    | Value             |
| -------- | ----------------- |
| Email    | `admin@gmail.com` |
| Password | `123456`          |
| Role     | `SUPER_ADMIN`     |

> Change the default password in production.

---

## Authentication

The API uses **stateless JWT authentication**.

### Login

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin@gmail.com",
  "password": "123456"
}
```

**Response:**

- `accessToken` — JWT sent in the response body; include it in subsequent requests
- `refresh_token` — HTTP-only, Secure cookie used to obtain a new access token
- `user` — Logged-in user profile (id, email, name, role)

### Using the access token

```http
Authorization: Bearer <accessToken>
```

### Refresh token

```http
GET /api/v1/auth/refresh
Cookie: refresh_token=<token>
```

Returns a new access token and rotates the refresh token cookie.

### Logout

```http
POST /api/v1/auth/logout
Authorization: Bearer <accessToken>
```

Clears the refresh token from the database and removes the cookie.

### Register

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

New users are assigned the `USER` role automatically.

### Get current account

```http
GET /api/v1/auth/account
Authorization: Bearer <accessToken>
```

---

## Roles & Authorization

| Role            | Capabilities                                                                 |
| --------------- | ---------------------------------------------------------------------------- |
| **SUPER_ADMIN** | Full access: users, roles, companies, skills, jobs, resumes, email broadcast |
| **HR**          | Manage jobs and company info; view/update resumes for their company          |
| **USER**        | Register, apply for jobs (resumes), manage subscriptions, upload files       |

### Public endpoints (no authentication)

- `GET /`, `/storage/**`
- `POST /api/v1/auth/login`, `/api/v1/auth/register`
- `GET /api/v1/auth/refresh`
- `GET /api/v1/companies/**`, `/api/v1/jobs/**`, `/api/v1/skills/**`
- Swagger UI and OpenAPI docs

All other endpoints require a valid JWT.

---

## API Response Format

Successful responses are wrapped by `FormatRestResponse`:

```json
{
  "statusCode": 200,
  "error": null,
  "message": "Login Successful",
  "data": {}
}
```

Error responses (from `GlobalException`):

```json
{
  "statusCode": 400,
  "error": "Exception occurs...",
  "message": "Email already in use",
  "data": null
}
```

Paginated list responses use `ResPaginationDTO`:

```json
{
  "statusCode": 200,
  "message": "fetch all users",
  "data": {
    "meta": {
      "page": 1,
      "pageSize": 10,
      "pages": 5,
      "total": 50
    },
    "result": []
  }
}
```

---

## Pagination & Filtering

List endpoints support **Spring Data pagination** and **Spring Filter** dynamic queries.

### Pagination

Query parameters (1-indexed when `SPRING_DATA_WEB_PAGEABLE_ONE_INDEXED_PARAMETERS=true`):

```
GET /api/v1/jobs?page=1&size=10&sort=createdAt,desc
```

### Filtering

Use the `filter` query parameter with Spring Filter syntax:

```
GET /api/v1/jobs?filter=name~'Java' and salary>1000
GET /api/v1/users?filter=email~'gmail'
GET /api/v1/companies?filter=name~'FPT'
```

Supported on: `/users`, `/jobs`, `/companies`, `/skills`, `/roles`, `/resumes`.

---

## File Upload

```http
POST /api/v1/files
Authorization: Bearer <accessToken>
Content-Type: multipart/form-data

file=<binary>
folder=resume
```

**Allowed extensions:** `jpg`, `jpeg`, `png`, `doc`, `docx`

**Max size:** Configurable via `SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE` (default `50MB`).

Uploaded files are stored under `JOBHUNTER_UPLOAD_FILE_BASE_URI` and served at:

```
GET /storage/<folder>/<filename>
```

---

## Email Notifications

When a Super Admin calls:

```http
GET /api/v1/email
Authorization: Bearer <accessToken>
```

The system sends job-alert emails to all subscribers. Each subscriber receives jobs matching their subscribed skills, rendered via the Thymeleaf template `templates/job.html`.

Configure SMTP settings in your `.env` file before using this feature.

---

## API Endpoints

Base URL: `/api/v1`

### Authentication

| Method | Endpoint         | Auth   | Description                         |
| ------ | ---------------- | ------ | ----------------------------------- |
| POST   | `/auth/login`    | Public | Login and receive tokens            |
| POST   | `/auth/register` | Public | Register a new user                 |
| GET    | `/auth/refresh`  | Cookie | Refresh access token                |
| POST   | `/auth/logout`   | JWT    | Logout and invalidate refresh token |
| GET    | `/auth/account`  | JWT    | Get current user profile            |

### Users _(SUPER_ADMIN)_

| Method | Endpoint      | Description                      |
| ------ | ------------- | -------------------------------- |
| POST   | `/users`      | Create user                      |
| GET    | `/users`      | List users (filter + pagination) |
| GET    | `/users/{id}` | Get user by ID                   |
| PUT    | `/users`      | Update user                      |
| DELETE | `/users/{id}` | Delete user                      |

### Roles _(SUPER_ADMIN)_

| Method | Endpoint      | Description    |
| ------ | ------------- | -------------- |
| POST   | `/roles`      | Create role    |
| GET    | `/roles`      | List roles     |
| GET    | `/roles/{id}` | Get role by ID |
| PUT    | `/roles`      | Update role    |
| DELETE | `/roles/{id}` | Delete role    |

### Companies

| Method | Endpoint          | Auth            | Description       |
| ------ | ----------------- | --------------- | ----------------- |
| POST   | `/companies`      | SUPER_ADMIN     | Create company    |
| GET    | `/companies`      | Public          | List companies    |
| GET    | `/companies/{id}` | Public          | Get company by ID |
| PUT    | `/companies`      | SUPER_ADMIN, HR | Update company    |
| DELETE | `/companies/{id}` | SUPER_ADMIN     | Delete company    |

### Jobs

| Method | Endpoint     | Auth            | Description   |
| ------ | ------------ | --------------- | ------------- |
| POST   | `/jobs`      | SUPER_ADMIN, HR | Create job    |
| GET    | `/jobs`      | Public          | List jobs     |
| GET    | `/jobs/{id}` | Public          | Get job by ID |
| PUT    | `/jobs`      | SUPER_ADMIN, HR | Update job    |
| DELETE | `/jobs/{id}` | SUPER_ADMIN, HR | Delete job    |

**Job levels:** `INTERN`, `FRESHER`, `JUNIOR`, `MIDDLE`, `SENIOR`

### Skills

| Method | Endpoint       | Auth        | Description     |
| ------ | -------------- | ----------- | --------------- |
| POST   | `/skills`      | SUPER_ADMIN | Create skill    |
| GET    | `/skills`      | Public      | List skills     |
| GET    | `/skills/{id}` | Public      | Get skill by ID |
| PUT    | `/skills`      | SUPER_ADMIN | Update skill    |
| DELETE | `/skills/{id}` | SUPER_ADMIN | Delete skill    |

### Resumes

| Method | Endpoint           | Auth                  | Description                         |
| ------ | ------------------ | --------------------- | ----------------------------------- |
| POST   | `/resumes`         | SUPER_ADMIN, USER     | Submit application                  |
| GET    | `/resumes`         | SUPER_ADMIN, HR       | List resumes (scoped to HR company) |
| GET    | `/resumes/{id}`    | SUPER_ADMIN, HR, USER | Get resume by ID                    |
| PUT    | `/resumes`         | SUPER_ADMIN, HR       | Update resume status                |
| DELETE | `/resumes/{id}`    | SUPER_ADMIN, HR, USER | Delete resume                       |
| POST   | `/resumes/by-user` | SUPER_ADMIN, USER     | List resumes for current user       |

**Resume statuses:** `PENDING`, `REVIEWING`, `APPROVED`, `REJECTED`

### Subscribers

| Method | Endpoint              | Auth              | Description                     |
| ------ | --------------------- | ----------------- | ------------------------------- |
| POST   | `/subscribers`        | SUPER_ADMIN, USER | Create job alert subscription   |
| PUT    | `/subscribers`        | SUPER_ADMIN, USER | Update subscription skills      |
| POST   | `/subscribers/skills` | SUPER_ADMIN, USER | Get current user's subscription |

### Files

| Method | Endpoint | Auth                  | Description   |
| ------ | -------- | --------------------- | ------------- |
| POST   | `/files` | SUPER_ADMIN, HR, USER | Upload a file |

### Email

| Method | Endpoint | Auth        | Description                        |
| ------ | -------- | ----------- | ---------------------------------- |
| GET    | `/email` | SUPER_ADMIN | Send job alerts to all subscribers |

---

## API Documentation (Swagger)

Interactive API docs are available when the app is running:

| Resource     | URL                                   |
| ------------ | ------------------------------------- |
| Swagger UI   | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs     |

Use the **Authorize** button in Swagger UI and enter `Bearer <accessToken>` to test protected endpoints.

---

## Running Tests

```bash
# Windows
gradlew.bat test

# Linux/macOS
./gradlew test
```

---

## Domain Model Overview

```
Role ──< User >── Company
                  │
                  └──< Job >──< Skill
                        │
                        └──< Resume >── User

Subscriber >──< Skill
```

| Entity         | Key fields                                                                                        |
| -------------- | ------------------------------------------------------------------------------------------------- |
| **User**       | name, email, password, age, gender, address, company, role                                        |
| **Company**    | name, description, address, logo                                                                  |
| **Job**        | name, location, salary, quantity, level, description, startDate, endDate, active, company, skills |
| **Resume**     | email, url (file path), status, user, job                                                         |
| **Skill**      | name                                                                                              |
| **Role**       | name, description, active                                                                         |
| **Subscriber** | name, email, skills                                                                               |

---

## License

MIT License — see [OpenAPI config](src/main/java/org/example/jobhunter/config/OpenAPIConfig.java) for details.
