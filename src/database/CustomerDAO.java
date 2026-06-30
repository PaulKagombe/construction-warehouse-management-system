package database;

import model.Customer;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class CustomerDAO {

    //method to add new customer to the database
    public boolean addCustomer(Customer customer) {

        String sql =
                "INSERT INTO customers " +
                        "(customer_id, customer_name, phone) " +
                        "VALUES (?, ?, ?)";

        Connection connection = DBconnection.getConnection();
        if (connection == null) {
            System.out.println("Error saving customer: Database connection is unavailable.");
            return false;
        }
        try (connection;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, customer.getCustomerId());
            statement.setString(2, customer.getCustomerName());
            statement.setString(3, customer.getPhoneNumber());

            statement.executeUpdate();

            System.out.println("Customer saved successfully!");
            return true;

        } catch (SQLException e) {
            System.out.println("Error saving customer: " + e.getMessage());
            return false;
        }
    }
    // method to view customer details in the database
    public void viewCustomers() {

        String sql = "SELECT * FROM customers";

        Connection connection = DBconnection.getConnection();
        if (connection == null) {
            System.out.println("Error loading customers: Database connection is unavailable.");
            return;
        }
        try (connection;
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                System.out.println("\nCustomer ID: " + resultSet.getInt("customer_id"));
                System.out.println("Customer Name: " + resultSet.getString("customer_name"));
                System.out.println("Phone: " + resultSet.getString("phone"));
                System.out.println("----------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
    }
    // method to look for customer in the database
    public Customer findCustomerById(
            int customerId
    ) {

        String sql =
                "SELECT * FROM customers " +
                        "WHERE customer_id = ?";

        Connection connection = DBconnection.getConnection();
        if (connection == null) {
            System.out.println("Error finding customer: Database connection is unavailable.");
            return null;
        }
        try (connection;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Customer(
                            resultSet.getInt("customer_id"),
                            resultSet.getString("customer_name"),
                            resultSet.getString("phone")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding customer: " + e.getMessage());
        }

        return null;
    }
    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getString("phone")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET customer_name = ?, phone = ? " +
                "WHERE customer_id = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getPhoneNumber());
            stmt.setInt(3, customer.getCustomerId());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // Dashboard Statistics
    public int getTotalCustomers() {

        String sql = "SELECT COUNT(*) FROM customers";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

}