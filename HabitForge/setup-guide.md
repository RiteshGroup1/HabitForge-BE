# HabitForge Setup Guide

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher
- Google OAuth2 credentials

## Step 1: Database Setup

1. **Install PostgreSQL**
   ```bash
   # Ubuntu/Debian
   sudo apt-get install postgresql postgresql-contrib
   
   # macOS (using Homebrew)
   brew install postgresql
   brew services start postgresql
   
   # Windows
   # Download and install from https://www.postgresql.org/download/windows/
   ```

2. **Create Database**
   ```sql
   -- Connect to PostgreSQL
   psql -U postgres
   
   -- Create database
   CREATE DATABASE habitforge;
   
   -- Create user (optional)
   CREATE USER habitforge_user WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE habitforge TO habitforge_user;
   ```

## Step 2: Google OAuth2 Setup

1. **Go to Google Cloud Console**
   - Visit https://console.cloud.google.com/
   - Create a new project or select existing one

2. **Enable OAuth2 API**
   - Go to "APIs & Services" > "Library"
   - Search for "Google OAuth2 API"
   - Click "Enable"

3. **Create OAuth2 Credentials**
   - Go to "APIs & Services" > "Credentials"
   - Click "Create Credentials" > "OAuth 2.0 Client IDs"
   - Select "Web application"
   - Add authorized redirect URI: `http://localhost:8080/auth/google/callback`
   - Copy Client ID and Client Secret

## Step 3: Environment Configuration

1. **Create `.env` file** (optional)
   ```bash
   # Database Configuration
   DB_USERNAME=postgres
   DB_PASSWORD=your_database_password
   
   # Google OAuth2 Configuration
   GOOGLE_CLIENT_ID=your_google_client_id
   GOOGLE_CLIENT_SECRET=your_google_client_secret
   GOOGLE_REDIRECT_URI=http://localhost:8080/auth/google/callback
   
   # JWT Configuration
   JWT_SECRET=your_super_secret_key_at_least_256_bits_long
   JWT_EXPIRATION=86400000
   ```

2. **Or update `application.yml` directly**
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/habitforge
       username: postgres
       password: your_password
   
     security:
       oauth2:
         client:
           registration:
             google:
               client-id: your_google_client_id
               client-secret: your_google_client_secret
   
   jwt:
     secret: your_super_secret_key_at_least_256_bits_long
     expiration: 86400000
   ```

## Step 4: Build and Run

1. **Build the project**
   ```bash
   mvn clean compile
   ```

2. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

3. **Or build JAR and run**
   ```bash
   mvn clean package
   java -jar target/habit-tracker-1.0.0.jar
   ```

## Step 5: Test the Application

1. **Check application health**
   ```bash
   curl http://localhost:8080/auth/google/login
   ```

2. **Test OAuth2 flow**
   - Visit the returned Google OAuth2 URL
   - Complete authentication
   - You should receive JWT token in response

3. **Test API endpoints**
   - Use the provided `api-examples.http` file
   - Replace `{token}` with your actual JWT token

## Common Issues and Solutions

### Database Connection Issues
- **Error**: `Connection refused`
  - **Solution**: Ensure PostgreSQL is running and accessible
  - Check database URL, username, and password

### OAuth2 Issues
- **Error**: `redirect_uri_mismatch`
  - **Solution**: Ensure redirect URI in Google Console matches application
  - Check for trailing slashes or protocol differences

### JWT Issues
- **Error**: `JWT signature does not match`
  - **Solution**: Ensure JWT secret is consistent across restarts
  - Use a strong, consistent secret key

### Port Conflicts
- **Error**: `Port 8080 already in use`
  - **Solution**: Change port in `application.yml` or kill conflicting process

## Development Tips

### Hot Reload
- Use Spring Boot DevTools for automatic restarts
- Add to `pom.xml` if not present:
  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-devtools</artifactId>
      <scope>runtime</scope>
  </dependency>
  ```

### Database Schema
- The application uses `ddl-auto: update` for development
- For production, consider using Flyway or Liquibase for migrations

### Logging
- Check logs for detailed error information
- Adjust log levels in `application.yml` as needed

## Production Deployment

### Security Considerations
- Use environment variables for sensitive configuration
- Enable HTTPS in production
- Use a proper JWT secret (at least 256 bits)
- Consider using a secrets management system

### Database Optimization
- Set appropriate connection pool size
- Enable database connection pooling
- Consider read replicas for scaling

### Monitoring
- Add health checks
- Implement proper logging
- Consider APM tools like New Relic or DataDog

## Next Steps

1. **Explore the API**: Use the provided examples to test all endpoints
2. **Review the code**: Understand the architecture and implementation
3. **Add features**: Extend the application with new functionality
4. **Write tests**: Add unit and integration tests
5. **Deploy**: Set up production environment

## Support

For issues:
1. Check the application logs
2. Review the troubleshooting section
3. Create an issue in the repository with detailed information
