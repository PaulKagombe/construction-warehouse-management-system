package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection factory using DriverManager.
 * Each call returns a new Connection (thread-safe).
 * The MySQL driver handles connection pooling at the driver level.
 */
public class DBconnection {

    // Read configuration from environment variables with sensible defaults.
    private static final String URL = System.getenv().getOrDefault(
            "DB_URL",
            "jdbc:mysql://192.168.10.103:3306/construction_management"
    );
    private static final String USER = System.getenv().getOrDefault("DB_USER", "warehouse_user");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "warehouse123");

    /**
     * Returns a new JDBC Connection for each call (thread-safe).
     * Callers should close the connection when finished (try-with-resources recommended).
     * @return a new Connection, or null if connection fails
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}