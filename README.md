# Sport Facility Booking System

## Project Introduction

Sport Facility Booking System is a web application that allows users to:

- Search and book sports facilities
- View booking schedules
- Manage bookings
- Handle user authentication and authorization
- Manage facilities, bookings, and users through an admin dashboard

The project is developed with:

- **Backend:** Java Spring Boot
- **Frontend:** ReactJS, Bootstrap 5
- **Database:** Azure SQL Server Database

Project Structure Tree:
```
sport-facility-booking/
│
├── frontend/                     # ReactJS Frontend
│   ├── public/
│   ├── src/
│   │   ├── components/           # Reusable UI components
│   │   ├── services/             # API calling layer
│   │   ├── context/              # Global state/context
|   |   ├── App.js
|   |   ├── App.css
|   └── package.json
│
├── backend/                      # Spring Boot REST API
│   ├── src/main/java/it/ou/sportfacilitybooking
│   │   ├── controller/           # REST Controllers
│   │   ├── service/              # Business logic
│   │   ├── repository/           # JPA repositories
│   │   ├── entity/               # Database entities
│   │   ├── dto/                  # Request/Response DTO
│   │   ├── mapper/               # Mapper
│   │   ├── config/               # Security & configuration
│   │   ├── exception/            # Global exception handling
│   │   └── scheduler/            # Booking scheduler
│   │
│   ├── src/test/                 # Backend unit tests
│   └── pom.xml
│
├── database/
|
│
├── docs/
│   ├── test-plan.md
│   ├── test-report.md
│   ├── test-summary.md
│   └── screenshots/
│
├── weekly-report/
|
├── README.md
└── docker-compose.yml
```
---

# 1. Technologies Used

## 1.1 Backend
- Java 21 
- Spring Boot 3.3.5 
- Spring Data JPA (For database access)
- Spring Security (For authentication)
- JWT (JJWT 0.11.5) (For token-based auth)
- Flyway (Database migration)
- MS SQL Server JDBC (SQL Server driver)
- Cloudinary (Image/file storage)
- JaCoCo (Code coverage testing)

## 1.2 Frontend
- Node.js 18+ (Runtime)
- npm (Package Manager)
- React 18.2.0 
- React Bootstrap 2.8.0 (Bootstrap 5 integration)
- Bootstrap 5.3.0 (Confirmed)
- FullCalendar 6.1.20 (Calendar component)
- Axios 1.4.0 (HTTP client)
- React Router DOM 6.14.0 (Routing)
- Jest & React Testing Library (Testing)

---

# 2. Required Versions

| Tool | Version |
|------|------|
| Java | 21+ |
| Maven | 3.9+ |
| Node.js | 18+ |
| npm | 9+ |

---

# 3. Run Backend

## 3.1 Navigate to backend folder

```bash
cd backend
```

---

## 3.2 Install dependencies

```bash
mvn clean install
```

---

## 3.3 Configure application properties

Open file:

```txt
src/main/resources/application.properties
```

```properties
spring.datasource.url=YOUR_CLOUD_DATABASE_URL
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

---

## 3.4 Run backend server

```bash
mvn spring-boot:run
```

Or run the main Spring Boot application class from IDE.

---

## Backend URL

```txt
http://localhost:8080
```

---

# 4. Run Frontend

## 4.1 Navigate to frontend folder

```bash
cd frontend
```

---

## 4.2 Install dependencies

```bash
npm install
```

---

## 4.3 Start frontend application

```bash
npm start
```

---

## 4.4 Frontend URL

```txt
http://localhost:3000
```

---

# 5. Run Tests

## 5.1 Backend Tests

```bash
cd backend
```

```bash
mvn test
```

---

## 5.2 Frontend Tests

```bash
cd frontend
```

```bash
npm test 
```
or if you want to check test coverage

```bash
npx jest --coverage
```

---


# Notes

- Make sure Java, Maven, Node.js, and npm are installed before running the project.
- Ensure the database is accessible from your network.
- Backend must be started before frontend to allow API communication.
