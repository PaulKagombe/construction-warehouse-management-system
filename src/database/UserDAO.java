package database;

import model.User;
import util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;


public class UserDAO {

    public User login(String username, String password) {

        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");


                if (PasswordUtil.verifyPassword(password, hashedPassword)) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            hashedPassword,
                            rs.getString("role")
                    );
                }
            }

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
        }

        return null;
    }
    public boolean addUser(User user) {

        // Hash the password before saving
        String hashedPassword = PasswordUtil.hashPassword(user.getPassword());

        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.getRole());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            System.out.println("Error adding user: " + e.getMessage());
            return false;
        }
    }

    public boolean usernameExists(String username) {

        String sql = "SELECT user_id FROM users WHERE username = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (Exception e) {
            System.out.println("Error checking username: " + e.getMessage());
            return false;
        }
    }

    public void viewUsers() {

        String sql = "SELECT user_id, username, role FROM users";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n===== SYSTEM USERS =====");

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println("\nUser ID  : " + rs.getInt("user_id"));
                System.out.println("Username : " + rs.getString("username"));
                System.out.println("Role     : " + rs.getString("role"));
                System.out.println("----------------------------");
            }

            if (!found) {
                System.out.println("No users found.");
            }

        } catch (Exception e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Do not expose hashed passwords when listing users
                list.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        "",
                        rs.getString("role")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}