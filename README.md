# 💰 Finance Dashboard Backend

A **Spring Boot** backend for a finance dashboard system supporting financial record management, role-based access control, dashboard analytics, and JWT authentication.

Built as part of the **Zorvyn FinTech Backend Developer Intern Assessment**.

---

## 📋 Table of Contents

- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [Environment Variables](#-environment-variables)
- [API Documentation](#-api-documentation)
  - [Authentication](#1-authentication)
  - [User Management](#2-user-management-admin-only)
  - [Financial Records](#3-financial-records)
  - [Dashboard & Analytics](#4-dashboard--analytics)
- [Role-Based Access Control](#-role-based-access-control)
- [Validation & Error Handling](#-validation--error-handling)
- [Data Model](#-data-model)
- [Assumptions & Design Decisions](#-assumptions--design-decisions)
- [Optional Enhancements Implemented](#-optional-enhancements-implemented)
- [Project Structure](#-project-structure)

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 4.0.5 |
| **Security** | Spring Security + JWT (jjwt 0.11.5) |
| **Database** | MySQL 8 with Spring Data JPA / Hibernate |
| **Password Hashing** | BCrypt |
| **Build Tool** | Gradle |
| **Utility** | Lombok |

---

## 🏗 Architecture

The application follows a **layered architecture** with clear separation of concerns:

```
┌──────────────────────────────────────────────────────┐
│                   Client (REST)                      │
└────────────────────────┬─────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────┐
│              Security Filter Chain                   │
│         (JwtFilter → Role Extraction)                │
└────────────────────────┬─────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────┐
│                  Controllers                         │
│    UserController │ RecordController │ Dashboard      │
│     (@PreAuthorize role-based guards)                │
└────────────────────────┬─────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────┐
│                   Services                           │
│   UserService │ RecordService │ DashboardService     │
│         (Business logic & validation)                │
└────────────────────────┬─────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────┐
│                 Repositories (JPA)                    │
│  UserRepository │ RecordRepository │ DashboardRepo   │
└────────────────────────┬─────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────┐
│                    MySQL Database                    │
│              users │ records tables                  │
└──────────────────────────────────────────────────────┘
```

**Key design patterns used:**
- **DTO Pattern** — Request/Response DTOs decouple API contracts from entities
- **Mapper Pattern** — Dedicated mapper classes handle entity ↔ DTO conversion
- **Repository Pattern** — Spring Data JPA repositories abstract data access
- **Filter Chain** — JWT authentication implemented as a servlet filter
- **Global Exception Handling** — `@RestControllerAdvice` for consistent error responses

---

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **MySQL 8** running locally (or remote)
- **Gradle** (wrapper included)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yash-sharma-js/finance-backend.git
   cd finance-backend
   ```

2. **Create the MySQL database**
   ```sql
   CREATE DATABASE finance_db;
   ```

3. **Configure environment variables**

   Create a `.env` file in the project root (or set environment variables):
   ```env
   JWT_SECRET=your-very-secret-key-that-is-at-least-32-characters-long
   ```

4. **Update database credentials** (if different from defaults)

   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/finance_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

5. **Build and run**
   ```bash
   ./gradlew bootRun
   ```

   The server starts at `http://localhost:8080`

6. **Verify**
   ```bash
   curl http://localhost:8080/api/v1/test
   # Expected: 200 Status
   ```

---

## 🔐 Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `JWT_SECRET` | Secret key for signing JWT tokens (min 32 characters) | ✅ |

Database credentials are configured in `application.properties`. Hibernate is set to `ddl-auto=update`, so tables are created/updated automatically on startup.

---

## 📖 API Documentation

**Base URL:** `http://localhost:8080/api/v1`

All protected endpoints require the `Authorization` header:
```
Authorization: Bearer <jwt_token>
```

---

### 1. Authentication

Authentication endpoints are **public** — no JWT required.

#### `POST /auth/register`

Register a new user. New users are assigned the **VIEWER** role by default.

**Request Body:**
```json
{
  "name": "Yash Sharma",
  "email": "yash@example.com",
  "password": "securePass123"
}
```

**Success Response (200):**
```json
{
  "id": 1,
  "name": "Yash Sharma",
  "email": "yash@example.com",
  "role": "VIEWER",
  "isActive": true
}
```

**Error Response (400):**
```json
{
  "status": 400,
  "message": "Email already exists",
  "timestamp": "2026-04-04T13:00:00"
}
```

---

#### `POST /auth/login`

Authenticate and receive a JWT token.

**Request Body:**
```json
{
  "email": "yash@example.com",
  "password": "securePass123"
}
```

**Success Response (200):**
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ5YXNoQGV4YW1wbGUuY29tIiwicm9sZSI6IlZJRVdFUiIsImlhdCI6MTcxMjIzNDU2NywiZXhwIjoxNzEyMzIwOTY3fQ...
```

The JWT token contains:
- `sub` — user email
- `role` — user role (VIEWER, ANALYST, ADMIN)
- `iat` — issued at timestamp
- `exp` — expiration (24 hours)

---

### 2. User Management (Admin Only)

All user management endpoints require **ADMIN** role.

#### `GET /users`

Retrieve all registered users.

**Response (200):**
```json
[
  {
    "id": 1,
    "name": "Yash Sharma",
    "email": "yash@example.com",
    "role": "ADMIN",
    "isActive": true
  },
  {
    "id": 2,
    "name": "John Doe",
    "email": "john@example.com",
    "role": "VIEWER",
    "isActive": true
  }
]
```

---

#### `GET /users/{id}`

Retrieve a specific user by ID.

**Response (200):**
```json
{
  "id": 2,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "VIEWER",
  "isActive": true
}
```

---

#### `PATCH /users/{id}/role?role=ANALYST`

Update a user's role.

**Query Parameter:** `role` — one of `VIEWER`, `ANALYST`, `ADMIN`

**Response (200):**
```json
{
  "id": 2,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "ANALYST",
  "isActive": true
}
```

---

#### `PATCH /users/{id}/status?active=false`

Activate or deactivate a user account.

**Query Parameter:** `active` — `true` or `false`

**Response (200):**
```json
{
  "id": 2,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "ANALYST",
  "isActive": false
}
```

---

### 3. Financial Records

#### `POST /records` — *Admin Only*

Create a new financial record.

**Request Body:**
```json
{
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-01",
  "note": "Monthly salary deposit"
}
```

**Response (200):**
```json
{
  "id": 1,
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-01",
  "note": "Monthly salary deposit",
  "createdBy": "yash@example.com",
  "createdAt": "2026-04-01T10:30:00"
}
```

---

#### `GET /records` — *Analyst, Admin*

Get financial records for the authenticated user.

**Query Parameters (optional filters):**

| Parameter | Type | Description |
|-----------|------|-------------|
| `type` | String | Filter by `INCOME` or `EXPENSE` |
| `category` | String | Filter by category name |
| `startDate` | String | Filter from date (yyyy-MM-dd) |
| `endDate` | String | Filter to date (yyyy-MM-dd) |
| `page` | Integer | Page number (default: 0) |
| `size` | Integer | Page size (default: 10) |

**Example:** `GET /records?type=EXPENSE&category=Food&page=0&size=5`

**Response (200):**
```json
{
  "content": [
    {
      "id": 2,
      "amount": 250.00,
      "type": "EXPENSE",
      "category": "Food",
      "date": "2026-04-02",
      "note": "Grocery shopping",
      "createdBy": "yash@example.com",
      "createdAt": "2026-04-02T14:20:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 5
}
```

---

#### `GET /records/all` — *Admin Only*

Get all financial records across all users.

**Response (200):** Array of all record objects.

---

#### `PUT /records/{id}` — *Admin Only*

Update a financial record.

**Request Body:**
```json
{
  "amount": 6000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-01",
  "note": "Updated salary amount"
}
```

---

#### `DELETE /records/{id}` — *Admin Only*

Soft-delete a financial record (marks as deleted, does not permanently remove).

**Response (200):**
```json
"Deleted successfully"
```

---

### 4. Dashboard & Analytics

Dashboard endpoints return aggregated summary data. Accessible by **all authenticated users** (scoped to their own data).

#### `GET /dashboard/summary`

Get financial overview for the current user.

**Response (200):**
```json
{
  "totalIncome": 15000.00,
  "totalExpense": 4500.00,
  "netBalance": 10500.00
}
```

---

#### `GET /dashboard/categories`

Get category-wise spending/income breakdown.

**Response (200):**
```json
[
  { "category": "Salary", "total": 15000.00 },
  { "category": "Food", "total": 2500.00 },
  { "category": "Transport", "total": 1200.00 },
  { "category": "Utilities", "total": 800.00 }
]
```

---

#### `GET /dashboard/recent`

Get the 5 most recent financial records.

**Response (200):**
```json
[
  {
    "id": 5,
    "amount": 120.00,
    "type": "EXPENSE",
    "category": "Food",
    "date": "2026-04-04",
    "note": "Lunch"
  }
]
```

---

#### `GET /dashboard/trends`

Get monthly income/expense trends.

**Response (200):**
```json
[
  {
    "month": "2026-04",
    "income": 15000.00,
    "expense": 4500.00
  },
  {
    "month": "2026-03",
    "income": 14000.00,
    "expense": 5200.00
  }
]
```

---

## 🔒 Role-Based Access Control

The system enforces three user roles with tiered permissions:

| Role | Description |
|------|-------------|
| **VIEWER** | Can view dashboard summaries only. Cannot access raw records or modify data. |
| **ANALYST** | Can view dashboard data and read financial records. Cannot create, update, or delete records. |
| **ADMIN** | Full access — manage users, create/update/delete records, view all data. |

### Permission Matrix

| Endpoint | Method | VIEWER | ANALYST | ADMIN |
|----------|--------|--------|---------|-------|
| `/auth/register` | POST | ✅ Public | ✅ Public | ✅ Public |
| `/auth/login` | POST | ✅ Public | ✅ Public | ✅ Public |
| `/users` | GET | ❌ | ❌ | ✅ |
| `/users/{id}` | GET | ❌ | ❌ | ✅ |
| `/users/{id}/role` | PATCH | ❌ | ❌ | ✅ |
| `/users/{id}/status` | PATCH | ❌ | ❌ | ✅ |
| `/records` | POST | ❌ | ❌ | ✅ |
| `/records` | GET | ❌ | ✅ | ✅ |
| `/records/all` | GET | ❌ | ❌ | ✅ |
| `/records/{id}` | PUT | ❌ | ❌ | ✅ |
| `/records/{id}` | DELETE | ❌ | ❌ | ✅ |
| `/dashboard/summary` | GET | ✅ | ✅ | ✅ |
| `/dashboard/categories` | GET | ✅ | ✅ | ✅ |
| `/dashboard/recent` | GET | ✅ | ✅ | ✅ |
| `/dashboard/trends` | GET | ✅ | ✅ | ✅ |

### How Access Control Works

1. **JWT Token** — On login, the user receives a JWT containing their `email` and `role`.
2. **JwtFilter** — Every request passes through the filter, which extracts the role from the token and sets it as a Spring Security `GrantedAuthority` (e.g., `ROLE_ADMIN`).
3. **Method Security** — Controllers use `@PreAuthorize("hasRole('ADMIN')")` annotations to enforce role requirements at the endpoint level.
4. **URL Security** — The `SecurityFilterChain` in `AppConfig` provides an additional layer of URL-pattern-based access control.
5. **Ownership Checks** — Record operations verify the requesting user owns the record (Admin bypasses this).

---

## ⚠️ Validation & Error Handling

### Input Validation

All request DTOs use **Jakarta Bean Validation** annotations:

| Field | Validation |
|-------|-----------|
| `name` | `@NotBlank` — must not be empty |
| `email` | `@NotBlank`, `@Email` — valid email format |
| `password` | `@NotBlank`, `@Size(min=6)` — minimum 6 characters |
| `amount` | `@NotNull`, `@Positive` — must be a positive number |
| `type` | `@NotNull` — must be INCOME or EXPENSE |
| `category` | `@NotBlank` — must not be empty |
| `date` | `@NotNull` — must be a valid date |

### Error Response Format

All errors return a consistent JSON structure:

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-04-04T13:00:00",
  "errors": {
    "email": "must be a valid email address",
    "amount": "must be greater than 0"
  }
}
```

### HTTP Status Codes Used

| Code | Meaning | When |
|------|---------|------|
| `200` | OK | Successful operation |
| `400` | Bad Request | Validation failure, duplicate email, invalid input |
| `401` | Unauthorized | Missing or invalid JWT token |
| `403` | Forbidden | Insufficient role/permissions |
| `404` | Not Found | User or record not found |
| `500` | Internal Server Error | Unexpected server error |

---

## 🗄 Data Model

### Entity Relationship

```
┌───────────────┐        ┌───────────────────┐
│    users      │        │     records       │
├───────────────┤        ├───────────────────┤
│ id (PK)       │───┐    │ id (PK)           │
│ name          │   │    │ amount            │
│ email (UQ)    │   │    │ type (ENUM)       │
│ password      │   └───▶│ user_id (FK)      │
│ role (ENUM)   │        │ category          │
│ is_active     │        │ date              │
│ created_at    │        │ note              │
│               │        │ deleted           │
│               │        │ created_at        │
└───────────────┘        └───────────────────┘
```

### Users Table

| Column | Type | Constraints |
|--------|------|------------|
| `id` | BIGINT | Primary Key, Auto-increment |
| `name` | VARCHAR | Not Null |
| `email` | VARCHAR | Not Null, Unique |
| `password` | VARCHAR | Not Null (BCrypt hashed) |
| `role` | ENUM | VIEWER, ANALYST, ADMIN |
| `is_active` | BOOLEAN | Default: true |
| `created_at` | DATETIME | Auto-set on creation |

### Records Table

| Column | Type | Constraints |
|--------|------|------------|
| `id` | BIGINT | Primary Key, Auto-increment |
| `amount` | DOUBLE | Financial value |
| `type` | ENUM | INCOME, EXPENSE |
| `category` | VARCHAR | e.g., Salary, Food, Transport |
| `date` | DATE | Date of the transaction |
| `note` | VARCHAR | Optional description |
| `user_id` | BIGINT | Foreign Key → users.id |
| `deleted` | BOOLEAN | Soft delete flag (default: false) |
| `created_at` | DATETIME | Auto-set on creation |

---

## 💡 Assumptions & Design Decisions

### Assumptions

1. **Default Role** — New users are registered with the `VIEWER` role. An existing `ADMIN` must promote them to `ANALYST` or `ADMIN`.
2. **First Admin** — The first admin user must be created by directly updating the database or via a seed script, since only admins can change roles.
3. **Scoped Data** — Dashboard and record-listing endpoints return data **scoped to the authenticated user** (not global), ensuring data isolation.
4. **Single Currency** — All amounts are assumed to be in a single currency. No multi-currency support is implemented.
5. **Date Granularity** — Financial records use `LocalDate` (date-only, no time component). The `createdAt` audit field uses `LocalDateTime`.

### Design Decisions

| Decision | Rationale |
|----------|-----------|
| **JWT over Session** | Stateless authentication suits REST APIs better — no server-side session storage needed |
| **BCrypt for passwords** | Industry-standard adaptive hashing with automatic salting |
| **Soft delete for records** | Financial data should not be permanently destroyed; soft delete allows audit trail and recovery |
| **Separate DashboardRepository** | Although it queries the same `records` table, having a dedicated repository keeps analytics queries isolated from CRUD operations |
| **DTOs for all I/O** | Prevents entity exposure, controls serialized fields, and decouples API from persistence |
| **Mapper classes (not MapStruct)** | Manual mapping is simpler for this project size and avoids additional build dependencies |
| **Method-level security (`@PreAuthorize`)** | More granular and readable than URL-based security alone; combined with URL security for defense-in-depth |
| **Pagination on record listing** | Prevents large payloads when data grows; follows REST best practices |
| **`@RestControllerAdvice` for errors** | Centralizes error handling, provides consistent response format, prevents stack trace leaks |

### Tradeoffs

| Choice | Tradeoff |
|--------|----------|
| MySQL over SQLite | Requires a running MySQL instance, but provides production-grade features (proper ENUM, indexing, concurrent access) |
| Lombok | Reduces boilerplate significantly, but requires IDE plugin and annotation processing setup |
| Spring Boot 4.x | Latest features and security patches, but some deprecated API warnings with older JWT libraries |

---

## ✨ Optional Enhancements Implemented

| Enhancement | Status |
|-------------|--------|
| ✅ JWT Authentication | Token-based auth with 24h expiry |
| ✅ Password Hashing | BCrypt with automatic salting |
| ✅ Pagination | Configurable page size on record listing |
| ✅ Filtering | Records filterable by type, category, date range |
| ✅ Soft Delete | Records are marked deleted, not permanently removed |
| ✅ Input Validation | Bean Validation with field-level error messages |
| ✅ Global Error Handling | Consistent error response format |
| ✅ API Documentation | This README |
| ✅ User Status Management | Activate/deactivate users |

---

## 📁 Project Structure

```
src/main/java/com/github/finance_backend/
├── FinanceBackendApplication.java          # Application entry point
│
├── config/
│   └── AppConfig.java                      # Security config, password encoder, filter chain
│
├── security/
│   ├── JwtUtil.java                        # JWT token generation, parsing, validation
│   └── JwtFilter.java                      # JWT authentication filter with role extraction
│
├── exception/
│   ├── GlobalExceptionHandler.java         # Centralized error handling
│   └── ApiError.java                       # Standard error response DTO
│
├── user/
│   ├── controller/UserController.java      # Auth + user management endpoints
│   ├── service/UserService.java            # User business logic
│   ├── repository/UserRepository.java      # User data access
│   ├── entity/UserEntity.java              # User JPA entity
│   ├── enums/Role.java                     # VIEWER, ANALYST, ADMIN
│   ├── dto/UserRequestDTO.java             # Registration/login request
│   ├── dto/UserResponseDTO.java            # User response (no password)
│   └── mapper/UserMapper.java              # Entity ↔ DTO conversion
│
├── record/
│   ├── controller/RecordController.java    # Financial record CRUD + filtering
│   ├── service/RecordService.java          # Record business logic with access control
│   ├── repository/RecordRepository.java    # Record data access with filters
│   ├── entity/RecordEntity.java            # Record JPA entity
│   ├── enums/RecordType.java               # INCOME, EXPENSE
│   ├── dto/RecordRequestDTO.java           # Record creation/update request
│   ├── dto/RecordResponseDTO.java          # Record response with metadata
│   └── mapper/RecordMapper.java            # Entity ↔ DTO conversion
│
└── dashboard/
    ├── controller/DashboardController.java # Analytics endpoints
    ├── service/DashboardService.java       # Aggregation business logic
    └── repository/DashboardRepository.java # Aggregate queries (JPQL)
```

---

## 📬 Contact

**Yash Sharma**  
📧 yashsharma.js@gmail.com  
🔗 [GitHub](https://github.com/yash-sharma-js)

---

<p align="center">
  Built with ❤️ for Zorvyn FinTech
</p>
