# HabitForge - Production-Ready Habit Tracker Backend

A comprehensive habit tracking backend built with Spring Boot, featuring Google OAuth2 authentication, JWT tokens, gamification with smart leveling system, and interest-based analytics.

## 🚀 Features

### Core Features
- **Habit Management**: Create, update, delete, and track habits
- **Habit Logging**: Mark habits as complete/incomplete for specific dates
- **Duplicate Prevention**: Ensures no duplicate entries (habitId + date)
- **Progress Reports**: Weekly and monthly progress reports with success rates
- **Streak Tracking**: Current and longest streak calculations

### Advanced Features
- **Smart Leveling System**: Weighted scoring based on:
  - Completion Ratio (Consistency) - 50% weight
  - Total Completed Tasks (Effort) - 30% weight  
  - Current Streak (Discipline) - 20% weight
- **Category-Based Analytics**: Track performance across different habit categories
- **Interest Insights**: Get personalized insights about strongest/weakest categories

### Authentication & Security
- **Google OAuth2**: Single sign-on with Google accounts
- **JWT Tokens**: Stateless authentication with secure token generation
- **Spring Security**: Comprehensive security configuration
- **CORS Support**: Cross-origin resource sharing enabled

## 🛠 Tech Stack

- **Java 17+**
- **Spring Boot 3.2.0**
- **Spring Security 6**
- **Spring Data JPA (Hibernate)**
- **PostgreSQL**
- **Maven**
- **Lombok**
- **JWT (JJWT Library)**

## 📊 Database Design

### Entities
- **User**: id, name, email, provider, providerId, createdAt
- **Habit**: id, userId, name, description, category, frequency, createdAt
- **HabitLog**: id, habitId, date, status, createdAt (UNIQUE constraint on habitId+date)
- **UserProgress**: userId, totalPoints, level, currentStreak, longestStreak

### Relationships
- One User → Many Habits
- One Habit → Many HabitLogs
- One User → One UserProgress

## 🔧 Configuration

### Environment Variables
```bash
# Database
DB_USERNAME=postgres
DB_PASSWORD=your_password

# Google OAuth2
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GOOGLE_REDIRECT_URI=http://localhost:8080/auth/google/callback

# JWT
JWT_SECRET=your_super_secret_key_at_least_256_bits
JWT_EXPIRATION=86400000 # 24 hours in milliseconds
```

### Database Setup
1. Install PostgreSQL
2. Create database: `habitforge`
3. Configure connection in `application.yml`

## 🌐 API Endpoints

### Authentication
- `GET /auth/google/login` - Get Google OAuth2 login URL
- `GET /auth/google/callback` - OAuth2 callback (returns JWT)

### Habits
- `POST /habits` - Create new habit
- `GET /habits` - Get paginated habits for user
- `GET /habits/all` - Get all habits for user
- `GET /habits/{id}` - Get specific habit
- `PUT /habits/{id}` - Update habit
- `DELETE /habits/{id}` - Delete habit
- `GET /habits/category/{category}` - Get habits by category

### Habit Logging
- `POST /habit-log` - Create/update habit log
- `GET /habit-log/habit/{habitId}` - Get logs for specific habit
- `GET /habit-log/date-range` - Get logs for date range
- `GET /habit-log/date/{date}` - Get logs for specific date
- `GET /habit-log/today` - Get today's habit logs
- `GET /habit-log/stats/today` - Get today's completion stats

### Reports
- `GET /reports/weekly` - Get weekly progress report
- `GET /reports/monthly` - Get monthly progress report
- `GET /reports/custom?startDate=2024-01-01&endDate=2024-01-31` - Get custom date range report

### Progress & Analytics
- `GET /progress` - Get user progress (level, points, streak)
- `GET /progress/categories` - Get category-based analytics
- `POST /progress/refresh` - Refresh user progress

## 🎮 Gamification System

### Scoring Formula
```
score = (completionRatio * 50) + 
        (log(totalCompletedTasks + 1) * 30) + 
        (currentStreak * 2)
```

### Level Calculation
```
level = (score / 100) + 1
```

### Categories
- HEALTH, FITNESS, LEARNING, CAREER, PRODUCTIVITY
- SOCIAL, FINANCIAL, SPIRITUAL, CREATIVE, OTHER

## 📝 Sample API Usage

### 1. Google OAuth2 Login
```bash
# Get login URL
GET http://localhost:8080/auth/google/login

# Redirect to Google, then callback returns:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "provider": "GOOGLE"
  }
}
```

### 2. Create Habit
```bash
POST http://localhost:8080/habits
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "Morning Meditation",
  "description": "10 minutes of mindfulness meditation",
  "category": "HEALTH",
  "frequency": "DAILY"
}
```

### 3. Log Habit Completion
```bash
POST http://localhost:8080/habit-log
Authorization: Bearer {token}
Content-Type: application/json

{
  "habitId": 1,
  "date": "2024-01-15",
  "status": true
}
```

### 4. Get Progress
```bash
GET http://localhost:8080/progress
Authorization: Bearer {token}

{
  "id": 1,
  "totalPoints": 125,
  "level": 2,
  "currentStreak": 5,
  "longestStreak": 12,
  "totalCompletedTasks": 25,
  "completionRatio": 0.8
}
```

## 🚀 Getting Started

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd HabitForge
   ```

2. **Configure environment variables**
   - Set up Google OAuth2 credentials
   - Configure database connection
   - Set JWT secret

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the API**
   - Base URL: `http://localhost:8080`
   - Use Google OAuth2 for authentication
   - All protected endpoints require JWT token

## 🧪 Testing

Run tests with Maven:
```bash
mvn test
```

## 📝 Architecture

### Layer Structure
- **Controller**: REST API endpoints
- **Service**: Business logic and scoring algorithms
- **Repository**: Data access layer
- **Entity**: JPA entities
- **DTO**: Data transfer objects
- **Security**: Authentication and authorization
- **Exception**: Global error handling

### Design Patterns
- Clean Architecture with clear separation of concerns
- Repository pattern for data access
- DTO pattern for API contracts
- Service layer for business logic
- Global exception handling

## 🔒 Security Features

- **OAuth2 Integration**: Secure Google authentication
- **JWT Validation**: Stateless token authentication
- **CORS Configuration**: Cross-origin security
- **Input Validation**: Comprehensive request validation
- **Error Handling**: Secure error responses

## 📈 Scalability Considerations

- **Database Indexing**: Optimized queries with proper indexes
- **Pagination**: Efficient data retrieval
- **Caching**: Ready for Redis integration
- **Stateless Design**: Horizontal scaling ready
- **Connection Pooling**: Optimized database connections

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For issues and questions, please create an issue in the repository.
