# Construction Warehouse System

JavaFX-based warehouse management system for construction materials with MySQL backend.

## Requirements
- Java 21 (JDK)
- Maven 3.6+
- MySQL 8.0+ with construction_management database

## Configuration

Set environment variables (optional — defaults provided):

```powershell
$env:DB_URL='jdbc:mysql://192.168.10.103:3306/construction_management'
$env:DB_USER= your_username
$env:DB_PASSWORD=your_password
```

Defaults are embedded in the code; environment variables override them.

## Build

```powershell
cd D:\constructionwarehousesystem
mvn clean package
```

Output: `target/constructionwarehousesystem-1.0-shaded.jar` (fat JAR with all dependencies)

## Run

```powershell
java -jar target\constructionwarehousesystem-1.0-shaded.jar
```

## Features

- **Login**: Role-based access (Admin, Storekeeper, Sales)
- **Inventory**: Material tracking with low-stock alerts
- **Sales**: Transaction recording with revenue tracking
- **Suppliers**: Credit management and payment tracking
- **Dashboards**: Role-specific views with statistics

## Architecture

- **UI**: JavaFX 21
- **Database**: MySQL with JDBC
- **Security**: Passwords hashed with BCrypt
- **Connection**: Per-call DriverManager (thread-safe, MySQL driver handles pooling)

## Security Notes

- Credentials should be externalized in production (use environment variables or secrets manager)
- Do not commit credentials to version control
- Always use HTTPS for database connections in production
- Restrict database user permissions to minimum required

