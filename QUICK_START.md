# Quick Start Guide

## One-Command Build & Run

Open **Windows PowerShell** in the project directory and run:

```powershell
# 1. Clean build
mvn clean package

# 2. Run (if build succeeds)
java -jar target\constructionwarehousesystem-1.0-shaded.jar
```

## With Custom Database Configuration

```powershell
# Set environment variables
$env:DB_URL='jdbc:mysql://YOUR_HOST:3306/construction_management'
$env:DB_USER='your_user'
$env:DB_PASSWORD='your_password'

# Build
mvn clean package

# Run
java -jar target\constructionwarehousesystem-1.0-shaded.jar
```

## Login Credentials

Default test user (must exist in database):
- Username: `admin` (or any user in the `users` table)
- Password: (the plain text password for that user)

## Expected Output

**On successful build**:
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXs
[INFO] Finished at: ...
```

**On successful app start**:
- JavaFX window opens
- Login screen displays
- Database connection establishes (or error if DB unreachable)

## Troubleshooting

| Issue | Solution |
|-------|----------|
| `mvn: command not found` | Install Maven or add to PATH |
| Build fails to download dependencies | Check internet connection; Maven needs access to maven.org |
| Connection to database fails | Check MySQL is running; verify DB_URL, DB_USER, DB_PASSWORD |
| Login fails with valid credentials | Check database has `users` table populated |
| `ClassNotFoundException` for javax.sql | Update Java to 21+; maven-shade-plugin bundles everything |
| Warnings about `printStackTrace()` | Normal; app will still run fine |

## Project Structure

```
constructionwarehousesystem/
├── pom.xml                      (Maven configuration)
├── README.md                    (Full documentation)
├── CHANGES_SUMMARY.md           (What was changed)
├── src/
│   ├── database/               (DAO, DB connection)
│   ├── model/                  (Entity classes)
│   ├── service/                (Business logic)
│   ├── controller/             (Event handlers)
│   └── ui/                     (JavaFX screens)
└── target/
    └── constructionwarehousesystem-1.0-shaded.jar   (Runnable JAR)
```

## Database Setup (if needed)

Create tables (example):
```sql
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- ... other tables as per your schema
```

Then add a test user:
```sql
INSERT INTO users (username, password, role) VALUES 
('admin', '$2b$12$...', 'ADMIN');  -- Password hashed with BCrypt
```

## Next Steps

1. ✅ Build: `mvn clean package`
2. ✅ Run: `java -jar target\constructionwarehousesystem-1.0-shaded.jar`
3. ✅ Test login and dashboards
4. ✅ Test CRUD operations
5. ✅ Verify reports

---

For detailed documentation, see **README.md**

