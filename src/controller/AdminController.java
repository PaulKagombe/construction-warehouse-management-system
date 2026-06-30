package controller;

import database.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;

import java.time.LocalDate;

public class AdminController {

    private final MaterialDAo materialDAO = new MaterialDAo();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final SalesDAO salesDAO = new SalesDAO();
    private final UserDAO userDAO = new UserDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final SupplierCreditDAO supplierCreditDAO = new SupplierCreditDAO();

    // ===================================================
    // DASHBOARD STATS
    // ===================================================
    public int getTotalMaterials()   { return materialDAO.getAllMaterials().size(); }
    public int getTotalSuppliers()   { return supplierDAO.getAllSuppliers().size(); }
    public int getTotalCustomers()   { return customerDAO.getTotalCustomers(); }
    public int getTotalSales()       { return salesDAO.getTotalSalesCount(); }
    public double getTotalRevenue()  { return salesDAO.getTotalRevenue(); }
    public int getLowStockCount()    { return materialDAO.getLowStockCount(); }
    public double getInventoryValue(){ return materialDAO.getTotalInventoryValue(); }
    public int getTodaySales()       { return salesDAO.getTodaySalesCount(); }
    public double getTodayRevenue()  { return salesDAO.getTodayRevenue(); }
    public double getTotalCustomerDebt()  { return invoiceDAO.getTotalOutstandingBalance(); }
    public double getTotalSupplierDebt()  { return supplierCreditDAO.getTotalSupplierDebt(); }

    // ===================================================
    // MATERIALS
    // ===================================================
    public ObservableList<Material> getMaterials() {
        return FXCollections.observableArrayList(materialDAO.getAllMaterials());
    }

    public String addMaterial(int id, String name, String category, int qty,
                              double price, String unit, int minStock, int supplierId) {
        if (name.isBlank() || category.isBlank() || unit.isBlank())
            return "Please fill all fields.";
        if (!supplierDAO.supplierExists(supplierId))
            return "Supplier ID does not exist.";
        Material m = new Material(id, name, category, qty, price, unit, minStock, supplierId);
        return materialDAO.addMaterial(m) ? null : "Failed to save material.";
    }

    public String updateMaterial(Material original, String name, String category,
                                 int qty, double price, String unit,
                                 int minStock, int supplierId) {
        if (name.isBlank() || category.isBlank() || unit.isBlank())
            return "Please fill all fields.";
        if (!supplierDAO.supplierExists(supplierId))
            return "Supplier ID does not exist.";
        Material m = new Material(original.getMaterialId(), name, category,
                qty, price, unit, minStock, supplierId);
        return materialDAO.updateMaterial(m) ? null : "Failed to update material.";
    }

    public String deleteMaterial(int materialId) {
        return materialDAO.deleteMaterialById(materialId) ? null : "Failed to delete material.";
    }

    public Material findMaterialByName(String name) {
        return materialDAO.findMaterialByName(name);
    }

    public String getStockReport()    { return materialDAO.getStockReportText(); }
    public String getLowStockReport() { return materialDAO.getLowStockReportText(); }

    // ===================================================
    // SUPPLIERS
    // ===================================================
    public ObservableList<Supplier> getSuppliers() {
        return FXCollections.observableArrayList(supplierDAO.getAllSuppliers());
    }

    public String addSupplier(int id, String name, String phone,
                              String email, String location) {
        if (name.isBlank() || phone.isBlank() || email.isBlank() || location.isBlank())
            return "Please fill all fields.";
        if (!email.contains("@")) return "Invalid email address.";
        if (supplierDAO.supplierExists(id)) return "Supplier ID already exists.";
        return supplierDAO.addSupplier(new Supplier(id, name, phone, email, location))
                ? null : "Failed to save supplier.";
    }

    public String updateSupplier(Supplier original, String name, String phone,
                                 String email, String location) {
        if (name.isBlank() || phone.isBlank() || email.isBlank() || location.isBlank())
            return "Please fill all fields.";
        if (!email.contains("@")) return "Invalid email address.";
        return supplierDAO.updateSupplier(
                new Supplier(original.getSupplierId(), name, phone, email, location))
                ? null : "Failed to update supplier.";
    }

    public String deleteSupplier(int supplierId) {
        return supplierDAO.deleteSupplier(supplierId) ? null : "Failed to delete supplier.";
    }

    // ===================================================
    // CUSTOMERS
    // ===================================================
    public ObservableList<Customer> getCustomers() {
        return FXCollections.observableArrayList(customerDAO.getAllCustomers());
    }

    public String addCustomer(int id, String name, String phone) {
        if (name.isBlank() || phone.isBlank()) return "Please fill all fields.";
        return customerDAO.addCustomer(new Customer(id, name, phone))
                ? null : "Failed to save customer.";
    }

    public String updateCustomer(Customer original, String name, String phone) {
        if (name.isBlank() || phone.isBlank()) return "Please fill all fields.";
        return customerDAO.updateCustomer(new Customer(original.getCustomerId(), name, phone))
                ? null : "Failed to update customer.";
    }

    public String deleteCustomer(int customerId) {
        return customerDAO.deleteCustomer(customerId) ? null : "Failed to delete customer.";
    }

    public Customer findCustomer(int customerId) {
        return customerDAO.findCustomerById(customerId);
    }

    // ===================================================
    // SALES
    // ===================================================
    public ObservableList<Sale> getSales() {
        return FXCollections.observableArrayList(salesDAO.getAllSales());
    }

    public String getSalesReport() { return salesDAO.getSalesReportText(); }

    public String issueMaterial(int customerId, String materialName, int quantity) {
        Customer customer = customerDAO.findCustomerById(customerId);
        if (customer == null) return "Customer not found.";
        Material material = materialDAO.findMaterialByName(materialName);
        if (material == null) return "Material not found.";
        if (quantity <= 0) return "Quantity must be greater than zero.";
        if (quantity > material.getQuantity())
            return "Insufficient stock. Available: " + material.getQuantity();
        materialDAO.updateMaterialQuantity(
                material.getMaterialId(), material.getQuantity() - quantity);
        Sale sale = new Sale(0, customerId, material.getMaterialId(),
                material.getMaterialName(), quantity, material.getUnitPrice());
        return salesDAO.addSale(sale) ? null : "Failed to record sale.";
    }

    public String issueMaterialOnCredit(int customerId, String materialName,
                                        int quantity, LocalDate dueDate) {
        String err = issueMaterial(customerId, materialName, quantity);
        if (err != null) return err;
        Material material = materialDAO.findMaterialByName(materialName);
        if (material == null) return "Material not found after sale.";
        int saleId = salesDAO.getLastSaleId();
        double total = quantity * material.getUnitPrice();
        return invoiceDAO.createInvoice(customerId, saleId, total, dueDate)
                ? null : "Sale recorded but invoice creation failed.";
    }

    public int getLastSaleId() { return salesDAO.getLastSaleId(); }

    // ===================================================
    // USERS
    // ===================================================
    public ObservableList<User> getUsers() {
        return FXCollections.observableArrayList(userDAO.getAllUsers());
    }

    public String addUser(String username, String password, String role) {
        if (username.isBlank() || password.isBlank() || role == null)
            return "Please fill all fields.";
        if (userDAO.usernameExists(username)) return "Username already exists.";
        return userDAO.addUser(new User(0, username, password, role))
                ? null : "Failed to add user.";
    }

    public String deleteUser(User user, String currentUsername) {
        if (user.getUsername().equals(currentUsername))
            return "You cannot delete your own account.";
        return userDAO.deleteUser(user.getUserId()) ? null : "Failed to delete user.";
    }

    // ===================================================
    // INVOICES
    // ===================================================
    public ObservableList<Invoice> getAllInvoices() {
        return FXCollections.observableArrayList(invoiceDAO.getAllInvoices());
    }

    public ObservableList<Invoice> getOutstandingInvoices() {
        return FXCollections.observableArrayList(invoiceDAO.getOutstandingInvoices());
    }

    public ObservableList<Invoice> getOverdueInvoices() {
        return FXCollections.observableArrayList(invoiceDAO.getOverdueInvoices());
    }

    public String recordCustomerPayment(int invoiceId, int customerId,
                                        double amount, String method) {
        if (amount <= 0) return "Amount must be greater than zero.";
        return invoiceDAO.recordPayment(invoiceId, customerId, amount, method)
                ? null : "Failed to record payment.";
    }

    // ===================================================
    // SUPPLIER CREDIT
    // ===================================================
    public ObservableList<SupplierCredit> getAllSupplierCredits() {
        return FXCollections.observableArrayList(supplierCreditDAO.getAllSupplierCredits());
    }

    public ObservableList<SupplierCredit> getOutstandingSupplierCredits() {
        return FXCollections.observableArrayList(supplierCreditDAO.getOutstandingSupplierCredits());
    }

    public ObservableList<SupplierCredit> getOverdueSupplierCredits() {
        return FXCollections.observableArrayList(supplierCreditDAO.getOverdueSupplierCredits());
    }

    public String addSupplierCredit(int supplierId, String description,
                                    double amount, LocalDate dueDate) {
        if (description.isBlank()) return "Description is required.";
        if (amount <= 0) return "Amount must be greater than zero.";
        if (!supplierDAO.supplierExists(supplierId)) return "Supplier ID does not exist.";
        return supplierCreditDAO.addSupplierCredit(supplierId, description, amount, dueDate)
                ? null : "Failed to add credit.";
    }

    public String recordSupplierPayment(int creditId, int supplierId,
                                        double amount, String method) {
        if (amount <= 0) return "Amount must be greater than zero.";
        return supplierCreditDAO.recordPayment(creditId, supplierId, amount, method)
                ? null : "Failed to record payment.";
    }
}