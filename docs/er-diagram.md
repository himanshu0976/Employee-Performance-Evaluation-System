# Entity-Relationship (ER) Diagram

## Database Schema Model
The EPS persistence model is normalized in 3NF with foreign keys and cascade rules for relational integrity.

```mermaid
erDiagram
    DEPARTMENTS ||--o{ EMPLOYEES : "has staff"
    USERS ||--|| EMPLOYEES : "has employee profile"
    USERS ||--o{ EMPLOYEES : "manages"
    USERS ||--o{ EVALUATIONS : "evaluates"
    EMPLOYEES ||--o{ EVALUATIONS : "is evaluated in"
    EVALUATION_CYCLES ||--o{ EVALUATIONS : "contains"
    EVALUATIONS ||--|{ EVALUATION_SCORES : "contains scores"
    EVALUATION_CRITERIA ||--o{ EVALUATION_SCORES : "graded under"

    DEPARTMENTS {
        int id PK
        string name UK
        string code UK
        text description
        timestamp created_at
    }

    USERS {
        int id PK
        string username UK
        string password_hash
        enum role "ADMIN, MANAGER, EMPLOYEE"
        string full_name
        string email UK
        string phone
        boolean is_active
        timestamp created_at
        timestamp updated_at
    }

    EMPLOYEES {
        int id PK
        int user_id FK, UK
        string employee_code UK
        int department_id FK
        int manager_id FK
        string designation
        date hire_date
        decimal salary
        timestamp created_at
    }

    EVALUATION_CYCLES {
        int id PK
        string title
        text description
        date start_date
        date end_date
        enum status "UPCOMING, ACTIVE, COMPLETED"
        timestamp created_at
    }

    EVALUATION_CRITERIA {
        int id PK
        string name UK
        text description
        decimal weightage
        boolean is_active
        timestamp created_at
    }

    EVALUATIONS {
        int id PK
        int cycle_id FK
        int employee_id FK
        int evaluator_id FK
        decimal total_score
        string rating
        enum status "DRAFT, SUBMITTED, APPROVED"
        text summary_feedback
        text strengths
        text areas_for_improvement
        timestamp evaluated_at
        timestamp created_at
        timestamp updated_at
    }

    EVALUATION_SCORES {
        int id PK
        int evaluation_id FK
        int criteria_id FK
        decimal score
        text feedback
    }
```

---

## Table Relationships & Cardinality

1. **`users` &rarr; `employees` (1 : 1)**
   - Every employee has a corresponding user record containing login credentials (`user_id`).
2. **`departments` &rarr; `employees` (1 : N)**
   - A department can house multiple employees. If a department is deleted, employee department is set to NULL.
3. **`users` (Manager) &rarr; `employees` (1 : N)**
   - A manager manages multiple employees (`manager_id` references `users.id`).
4. **`evaluation_cycles` &rarr; `evaluations` (1 : N)**
   - A cycle groups all reviews for a designated period (e.g. Annual 2025, Mid-Year 2026).
5. **`employees` &rarr; `evaluations` (1 : N)**
   - An employee has at most one evaluation per review cycle (`UNIQUE(cycle_id, employee_id)`).
6. **`evaluations` &rarr; `evaluation_scores` (1 : N)**
   - An evaluation contains score entries for active criteria (`Attendance`, `Work Quality`, `Productivity`, `Teamwork`, `Communication`, `Goal Achievement`).
7. **`evaluation_criteria` &rarr; `evaluation_scores` (1 : N)**
   - Each score line item is tied to a specific criterion dimension and weightage.
