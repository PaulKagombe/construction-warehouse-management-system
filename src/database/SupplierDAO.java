package database;
import model.Supplier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import  java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

public class SupplierDAO {
    public boolean addSupplier(Supplier supplier) {

        String sql = "INSERT INTO suppliers (supplier_id, supplier_name, phone, email, location) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, supplier.getSupplierId());
            stmt.setString(2, supplier.getSupplierName());
            stmt.setString(3, supplier.getPhoneNumber());
            stmt.setString(4, supplier.getEmail());
            stmt.setString(5, supplier.getLocation());

            int rows = stmt.executeUpdate();

            return rows > 0; //  SUCCESS/FAIL SIGNAL

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void viewSuppliers() {

        String sql = "SELECT * FROM suppliers";

        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            boolean found = false;
            while (resultSet.next()) {
                found = true;
                System.out.println("\n===== SUPPLIER =====");
                System.out.println("Supplier ID: " + resultSet.getInt("supplier_id"));
                System.out.println("Supplier Name: " + resultSet.getString("supplier_name"));
                System.out.println("Phone Number: " + resultSet.getString("phone"));
                System.out.println("Email: " + resultSet.getString("email"));
                System.out.println("Location: " + resultSet.getString("location"));
                System.out.println("----------------------------");
            }

            if (!found) System.out.println("No suppliers found.");

        } catch (SQLException e) {
            System.out.println("Error retrieving suppliers: " + e.getMessage());
        }
    }
    public boolean supplierExists(int supplierId) {

        String sql =
                "SELECT supplier_id " +
                        "FROM suppliers " +
                        "WHERE supplier_id = ?";

        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, supplierId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            System.out.println("Error checking supplier: " + e.getMessage());
            return false;
        }
    }
    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM suppliers";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("location")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    public boolean deleteSupplier(int supplierId) {
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE suppliers SET supplier_name = ?, phone = ?, " +
                "email = ?, location = ? WHERE supplier_id = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, supplier.getSupplierName());
            stmt.setString(2, supplier.getPhoneNumber());
            stmt.setString(3, supplier.getEmail());
            stmt.setString(4, supplier.getLocation());
            stmt.setInt(5, supplier.getSupplierId());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
