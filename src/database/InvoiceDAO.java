package database;

import model.Invoice;
import model.CustomerPayment;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    // Create invoice after a credit sale
    public boolean createInvoice(int customerId, int saleId,
                                 double totalAmount, LocalDate dueDate) {
        String sql = "INSERT INTO invoices (customer_id, sale_id, total_amount, " +
                "balance, due_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, saleId);
            stmt.setDouble(3, totalAmount);
            stmt.setDouble(4, totalAmount);
            stmt.setDate(5, Date.valueOf(dueDate));
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error creating invoice: " + e.getMessage());
            return false;
        }
    }

    // Get all invoices with customer name
    public List<Invoice> getAllInvoices() {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, c.customer_name FROM invoices i " +
                "JOIN customers c ON i.customer_id = c.customer_id " +
                "ORDER BY i.invoice_date DESC";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Invoice(
                        rs.getInt("invoice_id"),
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getInt("sale_id"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("amount_paid"),
                        rs.getDouble("balance"),
                        rs.getString("status"),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getTimestamp("invoice_date").toLocalDateTime()
                ));
            }
        } catch (Exception e) {
            System.out.println("Error loading invoices: " + e.getMessage());
        }
        return list;
    }

    // Get unpaid/partial invoices only
    public List<Invoice> getOutstandingInvoices() {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, c.customer_name FROM invoices i " +
                "JOIN customers c ON i.customer_id = c.customer_id " +
                "WHERE i.status != 'PAID' ORDER BY i.due_date ASC";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Invoice(
                        rs.getInt("invoice_id"),
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getInt("sale_id"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("amount_paid"),
                        rs.getDouble("balance"),
                        rs.getString("status"),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getTimestamp("invoice_date").toLocalDateTime()
                ));
            }
        } catch (Exception e) {
            System.out.println("Error loading outstanding invoices: " + e.getMessage());
        }
        return list;
    }

    // Record a customer payment
    public boolean recordPayment(int invoiceId, int customerId,
                                 double amount, String paymentMethod) {
        Connection conn = null;
        try {
            conn = DBconnection.getConnection();
            conn.setAutoCommit(false);

            // Insert payment record
            String paymentSql = "INSERT INTO customer_payments " +
                    "(invoice_id, customer_id, amount, payment_method) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(paymentSql)) {
                stmt.setInt(1, invoiceId);
                stmt.setInt(2, customerId);
                stmt.setDouble(3, amount);
                stmt.setString(4, paymentMethod);
                stmt.executeUpdate();
            }

            // Update invoice balance and status
            String updateSql = "UPDATE invoices SET " +
                    "amount_paid = amount_paid + ?, " +
                    "balance = balance - ?, " +
                    "status = CASE " +
                    "  WHEN balance - ? <= 0 THEN 'PAID' " +
                    "  ELSE 'PARTIAL' END " +
                    "WHERE invoice_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setDouble(1, amount);
                stmt.setDouble(2, amount);
                stmt.setDouble(3, amount);
                stmt.setInt(4, invoiceId);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            System.out.println("Error recording payment: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (Exception e) {}
        }
    }

    // Get overdue invoices
    public List<Invoice> getOverdueInvoices() {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, c.customer_name FROM invoices i " +
                "JOIN customers c ON i.customer_id = c.customer_id " +
                "WHERE i.status != 'PAID' AND i.due_date < CURDATE() " +
                "ORDER BY i.due_date ASC";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Invoice(
                        rs.getInt("invoice_id"),
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getInt("sale_id"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("amount_paid"),
                        rs.getDouble("balance"),
                        rs.getString("status"),
                        rs.getDate("due_date").toLocalDate(),
                        rs.getTimestamp("invoice_date").toLocalDateTime()
                ));
            }
        } catch (Exception e) {
            System.out.println("Error loading overdue invoices: " + e.getMessage());
        }
        return list;
    }

    // Get total outstanding balance across all customers
    public double getTotalOutstandingBalance() {
        String sql = "SELECT SUM(balance) FROM invoices WHERE status != 'PAID'";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            System.out.println("Error getting total balance: " + e.getMessage());
        }
        return 0;
    }
}