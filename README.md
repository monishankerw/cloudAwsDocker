# Spring Boot JWT Authentication with Docker

This is a Spring Boot application with JWT authentication, MySQL database, and Docker support.

## Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- Docker 20.10.0 or higher
- Docker Compose 2.0.0 or higher

## Project Structure

```
cloudAwsDocker/
├── src/                         # Source code
├── .dockerignore               # Docker ignore file
├── Dockerfile                  # Docker configuration
├── docker-compose.yml          # Docker Compose configuration
├── pom.xml                     # Maven configuration
└── README.md                   # This file
```

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd cloudAwsDocker
```

### 2. Build the Application

```bash
mvn clean package
```

### 3. Docker Setup

#### Build and Run with Docker Compose

```bash
# Build and start all services
docker-compose up --build

# Run in detached mode
docker-compose up -d --build

# View logs
docker-compose logs -f
```

#### Stop the Application

```bash
docker-compose down

# Remove volumes (caution: deletes database data)
docker-compose down -v
```

### 4. Access the Application

- **Application**: http://localhost:8082
- **H2 Console** (if enabled): http://localhost:8082/h2-console
  - JDBC URL: `jdbc:mysql://localhost:3306/cloud_aws_docker`
  - Username: `appuser`
  - Password: `apppassword`

## API Endpoints

### Authentication and OTP

- `POST /api/v1/auth/register` - Register a new user with email and mobile
  ```json
  {
    "username": "johndoe",
    "email": "user@example.com",
    "mobile": "9876543210",
    "password": "Password@123"
  }
  ```

- `POST /api/v1/auth/verify-email` - Verify email with OTP
  ```json
  {
    "email": "user@example.com",
    "otp": "123456"
  }
  ```

- `POST /api/v1/auth/verify-mobile` - Verify mobile with OTP
  ```json
  {
    "mobile": "9876543210",
    "otp": "123456"
  }
  ```

- `POST /api/v1/auth/resend-otp` - Resend OTP
  ```json
  {
    "email": "user@example.com",  // or "mobile": "9876543210"
    "isEmailVerification": true   // or false for mobile verification
  }
  ```

### Authentication

- `POST /api/v1/auth/login` - User login
  ```json
  {
    "username": "user@example.com",
    "password": "password"
  }
  ```

### User Management (Protected Endpoints)

- `POST /api/v1/users` - Register a new user
  ```json
  {
    "name": "John Doe",
    "email": "user@example.com",
    "password": "password",
    "role": "ROLE_USER"
  }
  ```

- `GET /api/v1/users` - Get all users (requires ADMIN role)
- `GET /api/v1/users/{id}` - Get user by ID (requires ADMIN role or own profile)
- `PUT /api/v1/users/{id}` - Update user (requires ADMIN role or own profile)
- `DELETE /api/v1/users/{id}` - Delete user (requires ADMIN role)

## Email and SMS Configuration

### Email Setup

1. **Gmail SMTP Setup**:
   - Go to your Google Account settings
   - Navigate to "Security" > "App passwords"
   - Generate an app password for your application
   - Use this password in the `EMAIL_PASSWORD` environment variable

2. **Environment Variables**:
   ```
   # Email Configuration
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-specific-password
   
   # AWS Configuration (for SMS)
   AWS_ACCESS_KEY_ID=your-aws-access-key
   AWS_SECRET_ACCESS_KEY=your-aws-secret-key
   AWS_REGION=ap-south-1
   ```

### SMS Setup (AWS SNS)

1. **AWS Setup**:
   - Create an AWS account if you don't have one
   - Create an IAM user with `AmazonSNSFullAccess` policy
   - Generate access keys for the IAM user
   - Set the AWS credentials in your environment variables

2. **Test Mode**:
   - You can enable test mode to log OTPs instead of sending them:
     ```yaml
     app:
       otp:
         test-mode: true  # Set to false to send real emails/SMS
     ```

### Testing Email and SMS

1. **Test Endpoints**:
   - Send test email: `POST /api/v1/test/send-email?email=your@email.com`
   - Send test SMS: `POST /api/v1/test/send-sms?phoneNumber=+1234567890`
   - Test full OTP flow: `POST /api/v1/test/send-otp?email=your@email.com&phoneNumber=+1234567890`

2. **Using cURL**:
   ```bash
   # Test email
   curl -X POST "http://localhost:8083/api/v1/test/send-email?email=your@email.com"
   
   # Test SMS
   curl -X POST "http://localhost:8083/api/v1/test/send-sms?phoneNumber=+1234567890"
   ```

3. **Testing in Browser**:
   - Open Swagger UI at `http://localhost:8083/swagger-ui.html`
   - Find the test endpoints under the `test-controller` section
   - Use the "Try it out" button to test the endpoints

## Environment Variables

You can configure the application using environment variables in the `docker-compose.yml` file:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/cloud_aws_docker
  SPRING_DATASOURCE_USERNAME: appuser
  SPRING_DATASOURCE_PASSWORD: apppassword
  JWT_SECRET: your-secret-key-here
  JWT_EXPIRATION_MS: 86400000  # 24 hours
```

## Running Tests

```bash
mvn test
```

## Development

### Database Migrations

Liquibase is used for database migrations. Place your migration files in `src/main/resources/db/changelog/`.

### Building for Production

```bash
mvn clean package -Pprod
```

## Troubleshooting

### Common Issues

1. **Port conflicts**: Make sure ports 8082 and 3306 are not in use.
   ```bash
   lsof -i :8082
   lsof -i :3306
   ```

2. **Docker build issues**: Try cleaning Docker cache
   ```bash
   docker system prune
   docker-compose build --no-cache
   ```

3. **Database connection issues**: Verify the MySQL container is running
   ```bash
   docker ps
   docker logs mysql_db
   ```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Docker Documentation](https://docs.docker.com/)
- [JWT Authentication](https://jwt.io/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
