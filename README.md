# Finance Dashboard Backend

A production-style Spring Boot backend for a finance dashboard system with role-based access control, JWT authentication, financial record management, and dashboard analytics.

---

## Tech Stack

| Layer        | Technology                                  |
|--------------|---------------------------------------------|
| Framework    | Spring Boot 3.2 (Java 21)                   |
| Security     | Spring Security + JWT (JJWT 0.12)           |
| Persistence  | Spring Data JPA + SQLite (via Hibernate)    |
| Validation   | Jakarta Bean Validation                     |
| Build        | Maven                                       |


---

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+

### Run

```bash
git clone <repo-url>
cd finance-dashboard
mvn spring-boot:run
```

Server starts at `http://localhost:8080`.

A SQLite database file `finance_dashboard.db` is created automatically in the project root.

### Run Tests

```bash
mvn test
```

---

## Default Seed Users

On first startup, three users are created automatically:

| Username  | Password     | Role     |
|-----------|-------------|---------|
| `admin`   | `admin123`  | ADMIN   |
| `analyst` | `analyst123`| ANALYST |
| `viewer`  | `viewer123` | VIEWER  |

---

## Role Permissions

| Action                        | VIEWER | ANALYST | ADMIN |
|-------------------------------|--------|---------|-------|
| View financial records        | ✅     | ✅      | ✅    |
| View dashboard summary        | ✅     | ✅      | ✅    |
| Create financial records      | ❌     | ✅      | ✅    |
| Update financial records      | ❌     | ✅      | ✅    |
| Delete financial records      | ❌     | ❌      | ✅    |
| Manage users (CRUD)           | ❌     | ❌      | ✅    |

---

## API Reference

### Authentication

#### Login
```
POST /api/auth/login
```
**Request:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```
**Response:**
```json
{
  "token": "<jwt>",
  "username": "admin",
  "role": "ADMIN"
}
```

Use the token as `Authorization: Bearer <token>` on all subsequent requests.

---

### Users  _(ADMIN only)_

| Method | Endpoint        | Description              |
|--------|----------------|--------------------------|
| GET    | `/api/users`    | List all users           |
| GET    | `/api/users/{id}` | Get user by ID         |
| POST   | `/api/users`    | Create a new user        |
| PATCH  | `/api/users/{id}` | Update role/status     |
| DELETE | `/api/users/{id}` | Deactivate user (soft) |

**Create User Request:**
```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret123",
  "role": "ANALYST"
}
```

**Update User Request (partial):**
```json
{
  "role": "VIEWER",
  "active": false
}
```

---

### Financial Records

| Method | Endpoint            | Access            | Description              |
|--------|--------------------|--------------------|--------------------------|
| GET    | `/api/records`      | All roles         | List records (filterable)|
| GET    | `/api/records/{id}` | All roles         | Get record by ID         |
| POST   | `/api/records`      | ANALYST, ADMIN    | Create record            |
| PUT    | `/api/records/{id}` | ANALYST, ADMIN    | Update record            |
| DELETE | `/api/records/{id}` | ADMIN             | Soft-delete record       |

**GET /api/records — Query Parameters:**

| Param      | Type   | Example         | Description              |
|------------|--------|-----------------|--------------------------|
| `type`     | enum   | `INCOME`        | Filter by INCOME/EXPENSE |
| `category` | string | `Salary`        | Filter by category       |
| `from`     | date   | `2024-01-01`    | Date range start         |
| `to`       | date   | `2024-12-31`    | Date range end           |
| `page`     | int    | `0`             | Page number (0-indexed)  |
| `size`     | int    | `20`            | Page size                |
| `sortBy`   | string | `date`          | Sort field               |
| `dir`      | string | `desc`          | asc / desc               |

**Create/Update Record Request:**
```json
{
  "amount": 1500.00,
  "type": "EXPENSE",
  "category": "Rent",
  "date": "2024-04-01",
  "notes": "Monthly rent payment"
}
```

---

### Dashboard Summary  _(All roles)_

```
GET /api/dashboard/summary
```

**Response:**
```json
{
  "totalIncome": 10000.00,
  "totalExpenses": 4500.00,
  "netBalance": 5500.00,
  "incomeByCategory": {
    "Salary": 8000.00,
    "Freelance": 2000.00
  },
  "expensesByCategory": {
    "Rent": 2000.00,
    "Food": 1500.00,
    "Utilities": 1000.00
  },
  "monthlyTrends": [
    {
      "month": "2024-04",
      "income": 5000.00,
      "expenses": 2250.00,
      "net": 2750.00
    }
  ],
  "recentActivity": [...]
}
```

---

## Error Responses

All errors follow a consistent shape:

```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": {
    "amount": "Amount must be positive",
    "date": "must not be null"
  },
  "timestamp": "2024-04-01T10:00:00"
}
```

| HTTP Status | Meaning                                  |
|-------------|------------------------------------------|
| 400         | Validation error / bad input             |
| 401         | Not authenticated / bad credentials      |
| 403         | Authenticated but insufficient role      |
| 404         | Resource not found                       |
| 409         | Conflict (duplicate username/email)      |
| 500         | Unexpected server error                  |

---

## Project Structure

```
src/main/java/com/finance/dashboard/
├── DashboardApplication.java       # Entry point
├── config/
│   ├── SecurityConfig.java         # Spring Security + JWT filter chain
│   └── DataSeeder.java             # Seeds default users on startup
├── controller/
│   ├── AuthController.java         # POST /api/auth/login
│   ├── UserController.java         # /api/users/**
│   ├── FinancialRecordController.java  # /api/records/**
│   └── DashboardController.java    # /api/dashboard/**
├── service/
│   ├── AuthService.java
│   ├── UserService.java
│   ├── FinancialRecordService.java
│   └── DashboardService.java
├── model/
│   ├── User.java
│   ├── Role.java                   # VIEWER | ANALYST | ADMIN
│   ├── FinancialRecord.java
│   └── TransactionType.java        # INCOME | EXPENSE
├── repository/
│   ├── UserRepository.java
│   └── FinancialRecordRepository.java   # Custom JPQL queries for filters + analytics
├── dto/                            # Request/Response objects
├── security/
│   ├── JwtUtil.java
│   ├── JwtAuthFilter.java
│   └── CustomUserDetailsService.java
└── exception/
    ├── GlobalExceptionHandler.java
    ├── ResourceNotFoundException.java
    └── ConflictException.java
```

---

## Design Decisions & Assumptions

**Soft Deletes:** Financial records are never hard-deleted. A `deleted` boolean flag is used instead. This preserves audit history. Users are also only deactivated, never removed.

**Role enforcement:** Enforced at two layers — URL-level in `SecurityConfig` and method-level via `@PreAuthorize` annotations. Both layers must agree, providing defense in depth.

**ANALYST write access:** ANALYSTs can create and update records (but not delete) since they are the primary data entry users in a finance context. Only ADMINs can delete.

**JWT expiry:** Tokens expire after 24 hours (configurable in `application.properties`).

**Pagination defaults:** Records default to page 0, size 20, sorted by date descending — sensible for a finance dashboard.

**`createdBy` tracking:** Every financial record stores which user created it, enabling future audit trail features.

**SQLite in production:** SQLite is used here for zero-config portability. For production, switch to PostgreSQL by updating the datasource in `application.properties` — no code changes needed.
