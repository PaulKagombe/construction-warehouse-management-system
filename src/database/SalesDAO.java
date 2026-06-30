package database;

import model.Sale;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class SalesDAO {

    public boolean addSale(Sale sale) {

        String sql =
                "INSERT INTO sales " +
                        "(customer_id, material_id, material_name, quantity, unit_price, total_amount) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, sale.getCustomerId());
            statement.setInt(2, sale.getMaterialId());
            statement.setString(3, sale.getMaterialName());
            statement.setInt(4, sale.getQuantity());
            statement.setDouble(5, sale.getUnitPrice());
            statement.setDouble(6, sale.getTotalAmount());

            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error saving sale: " + e.getMessage());
            return false;
        }
    }

    public void viewSales() {

        String sql = "SELECT * FROM sales";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            double totalRevenue = 0;
            int totalTransactions = 0;

            System.out.println("\n===== SALES REPORT =====");

            while (rs.next()) {

                totalTransactions++;

                int saleId = rs.getInt("sales_id");
                int customerId = rs.getInt("customer_id");
                String materialName = rs.getString("material_name");
                int quantity = rs.getInt("quantity");
                double unitPrice = rs.getDouble("unit_price");
                double totalAmount = rs.getDouble("total_amount");
                String saleDate = rs.getString("sale_date"); // fetch from DB

                totalRevenue += totalAmount;

                System.out.println("\nSale ID      : " + saleId);
                System.out.println("Customer ID  : " + customerId);
                System.out.println("Material     : " + materialName);
                System.out.println("Quantity     : " + quantity);
                System.out.println("Unit Price   : KES " + unitPrice);
                System.out.println("Total Amount : KES " + totalAmount);
                System.out.println("Date         : " + saleDate); // display it
                System.out.println("--------------------------------");
            }

            System.out.println("================================");
            System.out.println("Total Transactions : " + totalTransactions);
            System.out.println("Total Revenue      : KES " + totalRevenue);
            System.out.println("================================");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<Sale> getAllSales() {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT * FROM sales";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Sale(
                        rs.getInt("sales_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("material_id"),
                        rs.getString("material_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public String getSalesReportText() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== SALES REPORT =====\n\n");
        double totalRevenue = 0;
        int count = 0;

        for (Sale s : getAllSales()) {
            count++;
            totalRevenue += s.getTotalAmount();
            sb.append("Sale ID    : ").append(s.getSalesId()).append("\n");
            sb.append("Customer ID: ").append(s.getCustomerId()).append("\n");
            sb.append("Material   : ").append(s.getMaterialName()).append("\n");
            sb.append("Quantity   : ").append(s.getQuantity()).append("\n");
            sb.append("Unit Price : KES ").append(s.getUnitPrice()).append("\n");
            sb.append("Total      : KES ").append(s.getTotalAmount()).append("\n");
            sb.append("--------------------------------\n");
        }

        sb.append("\nTotal Transactions : ").append(count).append("\n");
        sb.append("Total Revenue      : KES ").append(totalRevenue).append("\n");
        return sb.toString();
    }
    public double getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) FROM sales";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int getTotalSalesCount() {
        String sql = "SELECT COUNT(*) FROM sales";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    public int getLastSaleId() {
        String sql = "SELECT MAX(sales_id) FROM sales";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    // Total sales transactions today
    public int getTodaySalesCount() {

        String sql = "SELECT COUNT(*) FROM sales WHERE DATE(sale_date) = CURDATE()";

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
    // Revenue generated today
    public double getTodayRevenue() {

        String sql = "SELECT COALESCE(SUM(total_amount),0) FROM sales WHERE DATE(sale_date) = CURDATE()";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}