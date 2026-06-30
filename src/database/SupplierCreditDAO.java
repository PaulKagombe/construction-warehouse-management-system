package database;

import model.SupplierCredit;
import model.SupplierPayment;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SupplierCreditDAO {

    // Record a new supplier credit (supplier delivered on credit)
    public boolean addSupplierCredit(int supplierId, String description,
                                     double totalAmount, LocalDate dueDate) {
        String sql = "INSERT INTO supplier_credit (supplier_id, description, " +
                "total_amount, balance, due_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            stmt.setString(2, description);
            stmt.setDouble(3, totalAmount);
            stmt.setDouble(4, totalAmount);
            stmt.setDate(5, Date.valueOf(dueDate));
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error adding supplier credit: " + e.getMessage());
            return false;
        }
    }

    // Get all supplier credits
    public List<SupplierCredit> getAllSupplierCredits() {
        List<SupplierCredit> list = new ArrayList<>();
        String sql = "SELECT sc.*, s.supplier_name FROM supplier_credit sc " +
                "JOIN suppliers s ON sc.supplier_id = s.supplier_id " +
                "ORDER BY sc.credit_date DESC";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new SupplierCredit(
                        rs.getInt("credit_id"),
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getString("description"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("amount_paid"),
                        rs.getDouble("balance"),
                        rs.getString("status"),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getTimestamp("credit_date").toLocalDateTime()
                ));
            }
        } catch (Exception e) {
            System.out.println("Error loading supplier credits: " + e.getMessage());
        }
        return list;
    }

    // Get outstanding supplier credits only
    public List<SupplierCredit> getOutstandingSupplierCredits() {
        List<SupplierCredit> list = new ArrayList<>();
        String sql = "SELECT sc.*, s.supplier_name FROM supplier_credit sc " +
                "JOIN suppliers s ON sc.supplier_id = s.supplier_id " +
                "WHERE sc.status != 'PAID' ORDER BY sc.due_date ASC";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new SupplierCredit(
                        rs.getInt("credit_id"),
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getString("description"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("amount_paid"),
                        rs.getDouble("balance"),
                        rs.getString("status"),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getTimestamp("credit_date").toLocalDateTime()
                ));
            }
        } catch (Exception e) {
            System.out.println("Error loading outstanding credits: " + e.getMessage());
        }
        return list;
    }

    // Record payment to supplier
    public boolean recordPayment(int creditId, int supplierId,
                                 double amount, String paymentMethod) {
        Connection conn = null;
        try {
            conn = DBconnection.getConnection();
            conn.setAutoCommit(false);

            // Insert payment record
            String paymentSql = "INSERT INTO supplier_payments " +
                    "(credit_id, supplier_id, amount, payment_method) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(paymentSql)) {
                stmt.setInt(1, creditId);
                stmt.setInt(2, supplierId);
                stmt.setDouble(3, amount);
                stmt.setString(4, paymentMethod);
                stmt.executeUpdate();
            }

            // Update credit balance and status
            String updateSql = "UPDATE supplier_credit SET " +
                    "amount_paid = amount_paid + ?, " +
                    "balance = balance - ?, " +
                    "status = CASE " +
                    "  WHEN balance - ? <= 0 THEN 'PAID' " +
                    "  ELSE 'PARTIAL' END " +
                    "WHERE credit_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setDouble(1, amount);
                stmt.setDouble(2, amount);
                stmt.setDouble(3, amount);
                stmt.setInt(4, creditId);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            System.out.println("Error recording supplier payment: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (Exception e) {}
        }
    }

    // Get overdue supplier credits
    public List<SupplierCredit> getOverdueSupplierCredits() {
        List<SupplierCredit> list = new ArrayList<>();
        String sql = "SELECT sc.*, s.supplier_name FROM supplier_credit sc " +
                "JOIN suppliers s ON sc.supplier_id = s.supplier_id " +
                "WHERE sc.status != 'PAID' AND sc.due_date < CURDATE() " +
                "ORDER BY sc.due_date ASC";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new SupplierCredit(
                        rs.getInt("credit_id"),
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getString("description"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("amount_paid"),
                        rs.getDouble("balance"),
                        rs.getString("status"),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getTimestamp("credit_date").toLocalDateTime()
                ));
            }
        } catch (Exception e) {
            System.out.println("Error loading overdue credits: " + e.getMessage());
        }
        return list;
    }

    // Get total amount owed to all suppliers
    public double getTotalSupplierDebt() {
        String sql = "SELECT SUM(balance) FROM supplier_credit WHERE status != 'PAID'";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            System.out.println("Error getting supplier debt: " + e.getMessage());
        }
        return 0;
    }
}