package controller;

import database.SupplierDAO;
import database.SupplierCreditDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Supplier;
import model.SupplierCredit;

import java.time.LocalDate;

public class SupplierController {

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final SupplierCreditDAO supplierCreditDAO = new SupplierCreditDAO();

    public ObservableList<Supplier> getAllSuppliers() {
        return FXCollections.observableArrayList(supplierDAO.getAllSuppliers());
    }

    public boolean addSupplier(Supplier supplier) {
        return supplierDAO.addSupplier(supplier);
    }

    public boolean updateSupplier(Supplier supplier) {
        return supplierDAO.updateSupplier(supplier);
    }

    public boolean deleteSupplier(int supplierId) {
        return supplierDAO.deleteSupplier(supplierId);
    }

    public boolean supplierExists(int supplierId) {
        return supplierDAO.supplierExists(supplierId);
    }

    public int getTotalSuppliers() {
        return supplierDAO.getAllSuppliers().size();
    }

    // Supplier Credit
    public ObservableList<SupplierCredit> getAllCredits() {
        return FXCollections.observableArrayList(supplierCreditDAO.getAllSupplierCredits());
    }

    public ObservableList<SupplierCredit> getOutstandingCredits() {
        return FXCollections.observableArrayList(supplierCreditDAO.getOutstandingSupplierCredits());
    }

    public ObservableList<SupplierCredit> getOverdueCredits() {
        return FXCollections.observableArrayList(supplierCreditDAO.getOverdueSupplierCredits());
    }

    public double getTotalSupplierDebt() {
        return supplierCreditDAO.getTotalSupplierDebt();
    }

    public String addCredit(int supplierId, String description,
                            double amount, LocalDate dueDate) {
        if (description.isBlank()) return "Description is required.";
        if (amount <= 0) return "Amount must be greater than zero.";
        if (!supplierExists(supplierId)) return "Supplier ID does not exist.";
        return supplierCreditDAO.addSupplierCredit(supplierId, description, amount, dueDate)
                ? null : "Failed to add credit.";
    }

    public String recordPayment(int creditId, int supplierId,
                                double amount, String method) {
        if (amount <= 0) return "Amount must be greater than zero.";
        return supplierCreditDAO.recordPayment(creditId, supplierId, amount, method)
                ? null : "Failed to record payment.";
    }
}