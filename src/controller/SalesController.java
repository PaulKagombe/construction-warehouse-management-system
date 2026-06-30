package controller;

import database.CustomerDAO;
import database.MaterialDAo;
import database.SalesDAO;
import database.InvoiceDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;
import model.Material;
import model.Sale;

import java.time.LocalDate;

public class SalesController {

    private final SalesDAO salesDAO = new SalesDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final MaterialDAo materialDAO = new MaterialDAo();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    public ObservableList<Sale> getAllSales() {
        return FXCollections.observableArrayList(salesDAO.getAllSales());
    }

    public int getTotalSalesCount() {
        return salesDAO.getTotalSalesCount();
    }

    public double getTotalRevenue() {
        return salesDAO.getTotalRevenue();
    }

    public int getTodaySalesCount() {
        return salesDAO.getTodaySalesCount();
    }

    public double getTodayRevenue() {
        return salesDAO.getTodayRevenue();
    }

    public String getSalesReport() {
        return salesDAO.getSalesReportText();
    }

    /**
     * Issues material to a customer.
     * Returns null on success, or an error message string.
     */
    public String issueMaterial(int customerId, String materialName, int quantity) {
        Customer customer = customerDAO.findCustomerById(customerId);
        if (customer == null) return "Customer not found.";

        Material material = materialDAO.findMaterialByName(materialName);
        if (material == null) return "Material not found.";

        if (quantity <= 0) return "Quantity must be greater than zero.";
        if (quantity > material.getQuantity())
            return "Insufficient stock. Available: " + material.getQuantity();

        materialDAO.updateMaterialQuantity(
                material.getMaterialId(),
                material.getQuantity() - quantity
        );

        Sale sale = new Sale(
                0, customerId,
                material.getMaterialId(),
                material.getMaterialName(),
                quantity,
                material.getUnitPrice()
        );

        return salesDAO.addSale(sale) ? null : "Failed to record sale.";
    }

    /**
     * Issues material and generates a credit invoice.
     */
    public String issueMaterialOnCredit(int customerId, String materialName,
                                        int quantity, LocalDate dueDate) {
        String result = issueMaterial(customerId, materialName, quantity);
        if (result != null) return result;

        Material material = materialDAO.findMaterialByName(materialName);
        if (material == null) return "Material not found after sale.";

        int saleId = salesDAO.getLastSaleId();
        double totalAmount = quantity * material.getUnitPrice();

        return invoiceDAO.createInvoice(customerId, saleId, totalAmount, dueDate)
                ? null : "Sale recorded but failed to create invoice.";
    }

    public Sale getLastSale() {
        int id = salesDAO.getLastSaleId();
        return salesDAO.getAllSales().stream()
                .filter(s -> s.getSalesId() == id)
                .findFirst()
                .orElse(null);
    }

    public Customer findCustomer(int customerId) {
        return customerDAO.findCustomerById(customerId);
    }

    public Material findMaterial(String name) {
        return materialDAO.findMaterialByName(name);
    }
}