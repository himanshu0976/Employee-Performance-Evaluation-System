# Employee Performance Evaluation System (EPS)

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.9-blue.svg)](https://maven.apache.org/)
[![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-red.svg)](https://jakarta.ee/)
[![Tomcat](https://img.shields.io/badge/Apache%20Tomcat-10.1+-yellow.svg)](https://tomcat.apache.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-purple.svg)](https://getbootstrap.com/)
[![Chart.js](https://img.shields.io/badge/Chart.js-4.4-pink.svg)](https://www.chartjs.org/)

An enterprise-grade **Employee Performance Evaluation System (EPS)** built with **Java 17**, **Jakarta Servlets 6.0**, **JSP 3.1 / JSTL 3.0**, **Apache Tomcat 10+**, **MySQL 8.0**, **JDBC**, **HikariCP**, **BCrypt**, **Bootstrap 5**, and **Chart.js**, strictly following the classical **Model-View-Controller (MVC)** architectural pattern without Spring Boot.

---

## Table of Contents
1. [Key Features & Role-Based Workflows](#key-features--role-based-workflows)
2. [Technology Stack](#technology-stack)
3. [Object-Oriented Programming (OOP) Demonstration](#object-oriented-programming-oop-demonstration)
4. [Evaluation Criteria & Automated Rating Scale](#evaluation-criteria--automated-rating-scale)
5. [Database Setup & Schema](#database-setup--schema)
6. [Building the Application](#building-the-application)
7. [Apache Tomcat 10+ Deployment](#apache-tomcat-10-deployment)
8. [Demo Accounts & Credentials](#demo-accounts--credentials)
9. [Project Directory Structure](#project-directory-structure)
10. [End-to-End Testing Flows](#end-to-end-testing-flows)
11. [Design Documentation](#design-documentation)

---

## Key Features & Role-Based Workflows

### 🛡️ System Administrator
- **Dashboard**: High-level overview of organization headcount, active review cycle status, department breakdown, and recent evaluations.
- **Departments CRUD**: Create, view, edit, and delete company departments with unique codes and descriptions.
- **Employees CRUD**: Onboard employees, manage credentials, job designations, salaries, hire dates, and active status.
- **Manager Assignment**: Assign and reassign reporting managers for individual employees or groups.
- **Evaluation Criteria Management**: Configure the 6 key performance criteria, descriptions, and dynamic weightages (summing to 100%).
- **Evaluation Cycle Management**: Schedule and govern review cycles (`UPCOMING`, `ACTIVE`, `COMPLETED`), specifying start and end dates.
- **Organization Reports & CSV Export**: Real-time performance dashboards, department comparisons, criteria radar averages, and RFC-4180 CSV report export.

### 👥 Manager (Evaluator)
- **Direct Reports View**: View all assigned employees and track evaluation status (`Pending`, `Draft`, `Submitted`) for active cycles.
- **Live Weighted Evaluation Form**: Score direct reports across the 6 criteria (1.0 to 5.0) with real-time score and rating tier preview via JavaScript.
- **Qualitative Feedback**: Add detailed feedback per criterion plus overarching summary, strengths, and areas for improvement.
- **Draft & Submit Modes**: Save partial drafts or submit finalized evaluations.

### 👤 Employee (Self-Service)
- **Personal Dashboard**: View latest evaluation score, rating badge, manager feedback, strengths, and improvement suggestions.
- **Competency Radar Chart**: Interactive Chart.js radar chart visualizing competencies across the 6 evaluation dimensions.
- **Evaluation History**: Chronological archive of all historical evaluations across previous review cycles.
- **Print / PDF View**: Dedicated clean layout for reviewing and printing individual performance appraisals.

### 🔒 Security & Architecture
- **BCrypt Password Hashing**: Passwords stored as salted BCrypt hashes (`$2a$10$...`) using `jbcrypt`.
- **Session Security**: Session fixation protection upon authentication, HTTP-only secure cookies (`EPS_JSESSIONID`), and 30-minute inactivity timeouts.
- **RBAC Enforcement**: `AuthFilter` intercepts all incoming requests and enforces role-based authorization for `/admin/*`, `/manager/*`, and `/employee/*`.
- **SQL Injection Prevention**: 100% Parameterized queries using JDBC `PreparedStatement`.

---

## Technology Stack

| Layer | Technology | Version | Purpose |
|---|---|---|---|
| **Language** | Java SE | 17 LTS | Core application runtime |
| **Build Tool** | Apache Maven | 3.9+ | Dependency management and WAR packaging |
| **Servlet Engine** | Jakarta Servlet API | 6.0.0 | Request controllers and security filters |
| **Server / Container** | Apache Tomcat | 10.1+ | Jakarta EE 10 compatible web server |
| **Presentation** | JSP & JSTL (GlassFish) | 3.1 / 3.0 | Server-side templating and views |
| **Styling & UI** | Bootstrap & Icons | 5.3.3 / 1.11 | Responsive modern user interface |
| **Data Visualization** | Chart.js | 4.4.2 | Interactive radar, bar, and doughnut charts |
| **Database** | MySQL Server | 8.0+ | Relational data persistence |
| **Connection Pool** | HikariCP | 5.1.0 | High-performance JDBC connection pooling |
| **Password Security** | jBCrypt | 0.4 | Salted one-way cryptographic password hashing |
| **JSON Serialization** | Google Gson | 2.10.1 | REST/JSON endpoints for Chart.js |
| **Testing** | JUnit 5 | 5.10.2 | Unit testing for business logic and algorithms |

---

## Object-Oriented Programming (OOP) Demonstration

The codebase is built with explicit adherence to core OOP principles:

1. **Abstract Classes & Inheritance**:
   - `abstract class User` defines common account attributes (`id`, `username`, `passwordHash`, `role`, `fullName`, `email`, `phone`, `active`).
   - `Admin`, `Manager`, and `Employee` inherit from `User`, encapsulating role-specific properties and behaviors.
2. **Polymorphism & Dynamic Dispatch**:
   - Abstract methods `getDashboardUrl()` and `getRoleDisplayName()` on `User` are polymorphically implemented by subclasses. Routing is resolved dynamically without type-checking switch statements.
   - Polymorphic instantiation in `UserDaoImpl.mapRowToUser()` produces concrete subclass instances based on the database role ENUM.
3. **Interface Abstraction**:
   - Service interfaces (`AuthService`, `DepartmentService`, `EmployeeService`, `EvaluationService`, `ReportService`, etc.) decouple business contracts from implementations (`*Impl.java`).
   - Generic DAO interface `GenericDao<T, ID>` establishes type-safe persistence contracts.
4. **Encapsulation**:
   - Domain logic and business rules are encapsulated inside models (e.g. `Evaluation.calculateTotalScoreAndRating()`, validation rules in `ValidationUtil`).
5. **Generics & Collections**:
   - Generic data structures (`List<EvaluationScore>`, `Map<Integer, EvaluationScore>`, `Optional<T>`) ensure strict compile-time type safety.
6. **Custom Exception Hierarchy**:
   - Base unchecked `AppException` extended by domain-specific exceptions: `AuthenticationException`, `AuthorizationException`, `ValidationException`, `ResourceNotFoundException`, `DuplicateResourceException`, and `DatabaseException`.

---

## Evaluation Criteria & Automated Rating Scale

### The 6 Performance Dimensions
Each employee is evaluated on 6 core criteria with configurable weights:
1. **Attendance** (15%): Punctuality, regularity, schedule adherence.
2. **Work Quality** (20%): Accuracy, attention to detail, quality of deliverables.
3. **Productivity** (20%): Efficiency, output volume, adherence to deadlines.
4. **Teamwork** (15%): Collaboration, cross-team synergy, willingness to help others.
5. **Communication** (15%): Clarity, transparency, active listening, constructive reporting.
6. **Goal Achievement** (15%): Attainment of assigned quarterly and annual KPIs.

### Automated Scoring Formula
$$\text{Total Score} = \frac{\sum_{i=1}^{6} (\text{Score}_i \times \text{Weight}_i)}{\sum_{i=1}^{6} \text{Weight}_i}$$

### Rating Scale
| Score Range | Rating Category | Badge Color |
|---|---|---|
| **4.50 – 5.00** | **Outstanding** | Success (Green) |
| **3.50 – 4.49** | **Exceeds Expectations** | Primary (Blue) |
| **2.50 – 3.49** | **Meets Expectations** | Info (Cyan) |
| **1.50 – 2.49** | **Needs Improvement** | Warning (Yellow/Orange) |
| **Below 1.50** | **Unsatisfactory** | Danger (Red) |

---

## Database Setup & Schema

### Step 1: Configure MySQL Connection
Review or update `src/main/resources/db.properties`:
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/eps_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
db.username=root
db.password=YOUR_MYSQL_PASSWORD

# HikariCP Connection Pool
db.pool.maximumPoolSize=10
db.pool.minimumIdle=2
db.pool.idleTimeout=300000
db.pool.connectionTimeout=20000
db.pool.maxLifetime=1200000
```
*(Environment variables `DB_URL`, `DB_USER`, and `DB_PASSWORD` can also be used as overrides).*

### Step 2: Initialize Database and Schema
Open a terminal / PowerShell and execute:
```bash
# 1. Create database and tables
mysql -u root -p < database/schema.sql

# 2. Insert sample seed data (1 admin, 2 managers, 10 employees, 3 departments, 2 cycles, evaluations)
mysql -u root -p < database/sample-data.sql
```

---

## Building the Application

Build the production WAR file using Apache Maven:
```bash
mvn clean package
```
This runs JUnit tests and generates the deployable artifact:
`target/eps.war`

---

### Method A: One-Command Embedded Tomcat 10+ Server (Immediate Run & Evaluation)
Run the application directly with zero external server setup using the embedded Tomcat 10.1+ runner:
```bash
mvn exec:java
```
To specify a custom HTTP port:
```bash
mvn exec:java -Dport=8080
```
The application will be accessible at:
- Application URL: **`http://localhost:8080/eps/`**
- Root Shortcut: **`http://localhost:8080/`**

### Method B: Deploy to External Apache Tomcat 10.1+
1. Build the production WAR file:
   ```bash
   mvn clean package
   ```
2. Copy the packaged WAR file to your Tomcat `webapps` directory:
   - On Windows:
     ```powershell
     Copy-Item "target\eps.war" "$env:USERPROFILE\.tomcat\apache-tomcat-10.1.20\webapps\eps.war"
     ```
   - On Linux / macOS:
     ```bash
     cp target/eps.war /path/to/tomcat10/webapps/eps.war
     ```
3. Start Tomcat:
   - On Windows:
     ```powershell
     & "$env:USERPROFILE\.tomcat\apache-tomcat-10.1.20\bin\startup.bat"
     ```
   - On Linux / macOS:
     ```bash
     /path/to/tomcat10/bin/startup.sh
     ```
4. Open your browser and navigate to:
   **`http://localhost:8080/eps/`**

### Method C: IDE Deployment (IntelliJ IDEA / Eclipse / VS Code)
Configure an **Apache Tomcat 10.1+** Local Server runtime pointing to `src/main/webapp` with context path `/eps` and JDK 17+.

---

## Demo Accounts & Credentials

The seed data provides pre-configured accounts with BCrypt-hashed passwords. On the login page (`/login`), click the quick-fill buttons or use the credentials below:

| Role | Username | Password | Full Name / Description | Default Redirect |
|---|---|---|---|---|
| **Admin** | `admin` | `Admin@123` | Eleanor Vance (System Admin) | `/admin/dashboard` |
| **Manager** | `manager1` | `Manager@123` | Sarah Connor (Engineering Manager) | `/manager/dashboard` |
| **Manager** | `manager2` | `Manager@123` | David Miller (Marketing & HR Manager) | `/manager/dashboard` |
| **Employee** | `emp1` | `Employee@123` | Alice Johnson (Senior Software Engineer) | `/employee/dashboard` |
| **Employee** | `emp2` | `Employee@123` | Bob Smith (QA Lead Engineer) | `/employee/dashboard` |
| **Employee** | `emp3` | `Employee@123` | Charlie Brown (DevOps Engineer) | `/employee/dashboard` |
| **Employee** | `emp4` | `Employee@123` | Diana Prince (Frontend Developer) | `/employee/dashboard` |
| **Employee** | `emp5` | `Employee@123` | Ethan Hunt (Junior Backend Developer) | `/employee/dashboard` |
| **Employee** | `emp6` | `Employee@123` | Fiona Gallagher (Senior Marketing Strategist) | `/employee/dashboard` |
| **Employee** | `emp7` | `Employee@123` | George Clark (Content & Brand Lead) | `/employee/dashboard` |
| **Employee** | `emp8` | `Employee@123` | Hannah Abbott (Growth & SEO Specialist) | `/employee/dashboard` |
| **Employee** | `emp9` | `Employee@123` | Ian Wright (HR Business Partner) | `/employee/dashboard` |
| **Employee** | `emp10` | `Employee@123` | Julia Roberts (Talent Acquisition Lead) | `/employee/dashboard` |

---

## Project Directory Structure

```text
EPS/
├── pom.xml                                   # Maven POM with Jakarta EE 10, Tomcat 10, MySQL 8, JSTL 3.0
├── README.md                                 # Complete project documentation and guide
├── database/
│   ├── schema.sql                            # 3NF relational schema with 7 tables, keys & constraints
│   └── sample-data.sql                       # Realistic seed data (Admin, Managers, 10 Employees, 2 Cycles)
├── docs/
│   ├── use-case-diagram.md                   # Mermaid use case diagram and actor specifications
│   ├── er-diagram.md                         # Mermaid ER diagram and table relationship details
│   ├── architecture.md                       # MVC architecture, layers, and OOP demonstration
│   └── presentation-outline.md               # 8-slide presentation walkthrough
└── src/
    ├── main/
    │   ├── java/com/eps/
    │   │   ├── controller/                   # Jakarta Servlets (@WebServlet)
    │   │   │   ├── AdminDashboardServlet.java
    │   │   │   ├── AdminReportServlet.java
    │   │   │   ├── AuthServlet.java
    │   │   │   ├── DepartmentServlet.java
    │   │   │   ├── EmployeeDashboardServlet.java
    │   │   │   ├── EmployeeEvaluationDetailServlet.java
    │   │   │   ├── EmployeeHistoryServlet.java
    │   │   │   ├── EmployeeServlet.java
    │   │   │   ├── EvaluationCriteriaServlet.java
    │   │   │   ├── EvaluationCycleServlet.java
    │   │   │   ├── HomeServlet.java
    │   │   │   ├── ManagerDashboardServlet.java
    │   │   │   └── ManagerEvaluationServlet.java
    │   │   ├── model/                        # Domain models demonstrating OOP inheritance
    │   │   │   ├── User.java (abstract)
    │   │   │   ├── Admin.java
    │   │   │   ├── Manager.java
    │   │   │   ├── Employee.java
    │   │   │   ├── Department.java
    │   │   │   ├── EvaluationCycle.java
    │   │   │   ├── EvaluationCriteria.java
    │   │   │   ├── Evaluation.java
    │   │   │   ├── EvaluationScore.java
    │   │   │   ├── Role.java (enum)
    │   │   │   ├── CriteriaAverageDTO.java
    │   │   │   ├── DepartmentReportDTO.java
    │   │   │   └── OrganizationStatsDTO.java
    │   │   ├── dao/                          # DAO interfaces & GenericDao<T, ID>
    │   │   │   ├── GenericDao.java
    │   │   │   ├── UserDao.java
    │   │   │   ├── DepartmentDao.java
    │   │   │   ├── EmployeeDao.java
    │   │   │   ├── EvaluationCriteriaDao.java
    │   │   │   ├── EvaluationCycleDao.java
    │   │   │   ├── EvaluationDao.java
    │   │   │   ├── EvaluationScoreDao.java
    │   │   │   └── ReportDao.java
    │   │   ├── dao/impl/                     # JDBC PreparedStatements implementations
    │   │   ├── service/                      # Business logic service interfaces
    │   │   ├── service/impl/                 # Scoring algorithms and service implementations
    │   │   ├── filter/                       # Security & Encoding Filters (@WebFilter)
    │   │   │   ├── AuthFilter.java
    │   │   │   └── EncodingFilter.java
    │   │   ├── util/                         # DB connection pooling, BCrypt, validation, CSV
    │   │   │   ├── DBUtil.java
    │   │   │   ├── PasswordUtil.java
    │   │   │   ├── ValidationUtil.java
    │   │   │   └── CsvExportUtil.java
    │   │   └── exception/                    # Custom exception hierarchy
    │   │       ├── AppException.java
    │   │       ├── AuthenticationException.java
    │   │       ├── AuthorizationException.java
    │   │       ├── DatabaseException.java
    │   │       ├── DuplicateResourceException.java
    │   │       ├── ResourceNotFoundException.java
    │   │       └── ValidationException.java
    │   ├── resources/
    │   │   ├── db.properties                 # Active database configuration
    │   │   └── db.properties.example         # Example configuration template
    │   └── webapp/
    │       ├── assets/
    │       │   ├── css/custom.css            # Custom CSS styling
    │       │   └── js/main.js                # JavaScript live score preview and toggles
    │       ├── WEB-INF/
    │       │   ├── web.xml                   # Deployment descriptor (session timeout, cookie config)
    │       │   └── views/
    │       │       ├── admin/                # Admin views (dashboard, departments, employees, etc.)
    │       │       ├── manager/              # Manager views (dashboard, evaluate-form, evaluations)
    │       │       ├── employee/             # Employee views (dashboard, history, evaluation-detail)
    │       │       ├── auth/                 # Login view
    │       │       └── common/               # Header, footer, alerts
    └── test/
        └── java/com/eps/                     # JUnit 5 unit tests for scoring & BCrypt
            ├── model/EvaluationTest.java
            └── util/PasswordUtilTest.java
```

---

## End-to-End Testing Flows

### 1. Admin Workflow
1. Navigate to `http://localhost:8080/eps/login` and log in with `admin` / `Admin@123`.
2. Inspect the **Dashboard**: view key metrics (employees, departments, managers, cycles).
3. Click **Departments**: add a new department (e.g. `Finance` / `FIN`), edit description, and verify.
4. Click **Employees**: onboard a new employee, set job designation, salary, department, and assign reporting manager.
5. Click **Criteria**: inspect the 6 core criteria, toggle status or adjust weightage.
6. Click **Cycles**: view active and completed cycles; create a new cycle or toggle status.
7. Click **Reports**: filter evaluations by cycle, department, or rating tier; view dynamic Chart.js charts; click **Export CSV** to download the report.

### 2. Manager Workflow
1. Log in with `manager1` / `Manager@123`.
2. On **Manager Dashboard**, view direct reports in Engineering team and review progress.
3. Click **Team Evaluations**: locate a pending or draft employee (e.g. `EMP-1003` - Charlie Brown).
4. Click **Evaluate / Edit**:
   - Change criteria scores from dropdowns (1.0 to 5.0).
   - Observe live score and rating badge updating dynamically.
   - Enter qualitative feedback for criteria, summary feedback, strengths, and improvement areas.
   - Click **Save Draft** to store work in progress, or **Submit Evaluation** to finalize.

### 3. Employee Workflow
1. Log in with `emp1` / `Employee@123` (Alice Johnson).
2. On **My Dashboard**, observe overall performance score (`4.85`), rating badge (`Outstanding`), and manager feedback.
3. Scroll down to inspect the interactive **Competency Radar Chart** mapping Attendance, Work Quality, Productivity, Teamwork, Communication, and Goal Achievement.
4. Click **Evaluation History**: review historical appraisals from previous review cycles.
5. Click **View Report**: open the full breakdown report suitable for saving or printing.

---

## Design Documentation

For in-depth architectural and structural diagrams, refer to the `docs/` directory:
- [Use Case Diagram & Actor Specifications](file:///docs/use-case-diagram.md)
- [Entity-Relationship (ER) Diagram & Schemas](file:///docs/er-diagram.md)
- [Architecture & OOP Design Patterns](file:///docs/architecture.md)
- [Project Presentation Slide Deck Outline](file:///docs/presentation-outline.md)
