# Doppler App

## Requirements
- Java 8 SDK
- PostgreSQL 10+

## Configuration
- Create a new database, e.g. `doppler`, you can use the GUI tool pgAdmin that usually comes with PostgreSQL installation
- Execute `sqls/ddl.sql` script to create the tables
- Update `src/main/resources/application.properties` (for deployment) and `src/test/resources/application.properties` (for testing) to match your environment, especially:
    - `spring.datasource.url` the database url
    - `spring.datasource.username` the database username
    - `spring.datasource.password` the database password
    - `jwt.secret` the secret to encode/decode JWT token
    - `jwt.expiration-in-seconds` the JWT token expiration
    - `token.m2m` the special token for other systems to call `scanTicket` endpoint
    - `notification.content.shareEvent` the notification content format for sharing event
    - `user-reward-point.description.registerEvent` the user reward point description format for registering event
    - `user-reward-point.description.scanTicket` the user reward point description format for scanning ticket
    - `user-reward-point.description.completeProfile` the user reward point description format for completing profile
    - `user-reward-point.description.submitSurvey` the user reward point description format for submitting survey
    - `user-reward-point.description.submitQuiz` the user reward point description format for submitting quiz
    - `user-reward-point.pointsForCompletingProfile` the points that user gains for completing profile

## Test Data

The below scripts can be used in API development phase to generate and delete test data:

- `src/test/resources/insert-test-data.sql`
- `src/test/resources/clear-test-data.sql`

## Testing

```bash
./mvnw clean test
```

## Deployment

```bash
./mvnw spring-boot:run
```

or build JAR and run

```
./mvnw clean package -DskipTests
java -jar target/doppler-api-1.0.0.jar
```

