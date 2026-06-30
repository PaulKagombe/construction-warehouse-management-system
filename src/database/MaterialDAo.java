package database;

import model.Material;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java . sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

public class MaterialDAo {
    public boolean addMaterial(Material material) {

        String sql =
                "INSERT INTO materials " +
                        "(material_id, material_name, category, quantity, unit_price, unit, minimum_stock, supplier_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, material.getMaterialId());
            statement.setString(2, material.getMaterialName());
            statement.setString(3, material.getCategory());
            statement.setInt(4, material.getQuantity());
            statement.setDouble(5, material.getUnitPrice());
            statement.setString(6, material.getUnitOfMeasure());
            statement.setInt(7, material.getMinimumStockLevel());
            statement.setInt(8, material.getSupplierId());

            statement.executeUpdate();

            System.out.println("Material saved successfully!");
            return true;

        } catch (SQLException e) {
            System.out.println("Error saving material: " + e.getMessage());
            return false;
        }
    }
     // method  to see all items in the database
        public void viewMaterials() {

            String sql = "SELECT * FROM materials";

                try (Connection connection = DBconnection.getConnection();
                     PreparedStatement statement = connection.prepareStatement(sql);
                     ResultSet resultSet = statement.executeQuery()) {

                    while (resultSet.next()) {
                        System.out.println("\nMaterial ID: " + resultSet.getInt("material_id"));
                        System.out.println("Material Name: " + resultSet.getString("material_name"));
                        System.out.println("Category: " + resultSet.getString("category"));
                        System.out.println("Quantity: " + resultSet.getInt("quantity"));
                        System.out.println("Unit Price: " + resultSet.getDouble("unit_price"));
                        System.out.println("Unit: " + resultSet.getString("unit"));
                        System.out.println("Minimum Stock: " + resultSet.getInt("minimum_stock"));
                        System.out.println("Supplier ID: " + resultSet.getInt("supplier_id"));
                        System.out.println("--------------------");
                    }

                } catch (SQLException e) {
                    System.out.println("Error loading materials: " + e.getMessage());
                }
        }
        // method to search for an item in the database
    public void searchMaterial(String materialName) {

        String sql =
                "SELECT * FROM materials " +
                        "WHERE material_name = ?";

        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, materialName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("\n===== MATERIAL FOUND =====");
                    System.out.println("Material ID: " + resultSet.getInt("material_id"));
                    System.out.println("Material Name: " + resultSet.getString("material_name"));
                    System.out.println("Category: " + resultSet.getString("category"));
                    System.out.println("Quantity: " + resultSet.getInt("quantity"));
                    System.out.println("Unit Price: " + resultSet.getDouble("unit_price"));
                    System.out.println("Supplier ID: " + resultSet.getInt("supplier_id"));
                } else {
                    System.out.println("Material not found.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching material: " + e.getMessage());
        }
    }
    //method to add items to the database
    public void receiveMaterial(
            String materialName,
            int quantityReceived
    ) {

        String sql =
                "UPDATE materials " +
                        "SET quantity = quantity + ? " +
                        "WHERE material_name = ?";

        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, quantityReceived);
            statement.setString(2, materialName);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Material quantity updated successfully.");
            } else {
                System.out.println("Material not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating material: " + e.getMessage());
        }
    }
    //method to search for an item in the database
    public Material findMaterialByName(String materialName) {

        String sql = "SELECT * FROM materials WHERE material_name = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, materialName);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                return new Material(
                        rs.getInt("material_id"),
                        rs.getString("material_name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getString("unit"),
                        rs.getInt("minimum_stock"),
                        rs.getInt("supplier_id")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }//update  method  for the records in the database
    public boolean updateMaterialQuantity(
            int materialId,
            int newQuantity
    ) {

        String sql =
                "UPDATE materials " +
                        "SET quantity = ? " +
                        "WHERE material_id = ?";

        try {

            Connection connection =
                    DBconnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setInt(1, newQuantity);
            statement.setInt(2, materialId);

            int rowsAffected =
                    statement.executeUpdate();

            statement.close();
            connection.close();

            return rowsAffected > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error updating quantity: "
                            + e.getMessage()
            );

            return false;
        }
    }
    // low stock alert
    public void lowStockReport() {
        String sql = "SELECT * FROM materials WHERE quantity < minimum_stock";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println("\nLOW STOCK ALERT");
                System.out.println("ID: " + rs.getInt("material_id"));
                System.out.println("Name: " + rs.getString("material_name"));
                System.out.println("Quantity: " + rs.getInt("quantity"));
                System.out.println("Min Stock: " + rs.getInt("minimum_stock"));
            }

            if (!found) {
                System.out.println("All materials are sufficiently stocked.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean deleteMaterialById(int materialId) {

        String sql = "DELETE FROM materials WHERE material_id = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, materialId);

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void stockReport() {

        String sql = "SELECT * FROM materials";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            double totalInventoryValue = 0;

            System.out.println("\n===== CURRENT STOCK REPORT =====");

            while (rs.next()) {

                int id = rs.getInt("material_id");
                String name = rs.getString("material_name");
                String category = rs.getString("category");
                int quantity = rs.getInt("quantity");
                double unitPrice = rs.getDouble("unit_price");

                double stockValue = quantity * unitPrice;
                totalInventoryValue += stockValue;

                System.out.println("\nMaterial ID: " + id);
                System.out.println("Name: " + name);
                System.out.println("Category: " + category);
                System.out.println("Quantity: " + quantity);
                System.out.println("Unit Price: " + unitPrice);
                System.out.println("Stock Value: " + stockValue);
                System.out.println("--------------------------------");
            }

            System.out.println("================================");
            System.out.println("TOTAL INVENTORY VALUE: KES " + totalInventoryValue);
            System.out.println("================================");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<Material> getAllMaterials() {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT * FROM materials";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                materials.add(new Material(
                        rs.getInt("material_id"),
                        rs.getString("material_name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getString("unit"),
                        rs.getInt("minimum_stock"),
                        rs.getInt("supplier_id")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return materials;
    }

    public String getStockReportText() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== CURRENT STOCK REPORT =====\n\n");
        double total = 0;

        for (Material m : getAllMaterials()) {
            double value = m.getQuantity() * m.getUnitPrice();
            total += value;
            sb.append("ID       : ").append(m.getMaterialId()).append("\n");
            sb.append("Name     : ").append(m.getMaterialName()).append("\n");
            sb.append("Category : ").append(m.getCategory()).append("\n");
            sb.append("Quantity : ").append(m.getQuantity()).append("\n");
            sb.append("Price    : KES ").append(m.getUnitPrice()).append("\n");
            sb.append("Value    : KES ").append(value).append("\n");
            sb.append("--------------------------------\n");
        }

        sb.append("\nTOTAL INVENTORY VALUE: KES ").append(total).append("\n");
        return sb.toString();
    }

    public String getLowStockReportText() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== LOW STOCK ALERTS =====\n\n");
        boolean found = false;

        for (Material m : getAllMaterials()) {
            if (m.getQuantity() < m.getMinimumStockLevel()) {
                found = true;
                sb.append("Name     : ").append(m.getMaterialName()).append("\n");
                sb.append("Quantity : ").append(m.getQuantity()).append("\n");
                sb.append("Min Stock: ").append(m.getMinimumStockLevel()).append("\n");
                sb.append("--------------------------------\n");
            }
        }

        if (!found) sb.append("All materials are sufficiently stocked.\n");
        return sb.toString();
    }
    public int getLowStockCount() {
        String sql = "SELECT COUNT(*) FROM materials WHERE quantity < minimum_stock";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public double getTotalInventoryValue() {
        String sql = "SELECT SUM(quantity * unit_price) FROM materials";
        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    public boolean updateMaterial(Material material) {
        String sql = "UPDATE materials SET material_name = ?, category = ?, " +
                "quantity = ?, unit_price = ?, unit = ?, " +
                "minimum_stock = ?, supplier_id = ? WHERE material_id = ?";

        try (Connection conn = DBconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, material.getMaterialName());
            stmt.setString(2, material.getCategory());
            stmt.setInt(3, material.getQuantity());
            stmt.setDouble(4, material.getUnitPrice());
            stmt.setString(5, material.getUnitOfMeasure());
            stmt.setInt(6, material.getMinimumStockLevel());
            stmt.setInt(7, material.getSupplierId());
            stmt.setInt(8, material.getMaterialId());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    }


