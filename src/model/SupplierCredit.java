package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SupplierCredit {
    private int creditId;
    private int supplierId;
    private String supplierName;
    private String description;
    private double totalAmount;
    private double amountPaid;
    private double balance;
    private String status;
    private LocalDate dueDate;
    private LocalDateTime creditDate;

    public SupplierCredit(int creditId, int supplierId, String supplierName,
                          String description, double totalAmount, double amountPaid,
                          double balance, String status, LocalDate dueDate,
                          LocalDateTime creditDate) {
        this.creditId = creditId;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.description = description;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.balance = balance;
        this.status = status;
        this.dueDate = dueDate;
        this.creditDate = creditDate;
    }

    public int getCreditId() { return creditId; }
    public int getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public String getDescription() { return description; }
    public double getTotalAmount() { return totalAmount; }
    public double getAmountPaid() { return amountPaid; }
    public double getBalance() { return balance; }
    public String getStatus() { return status; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDateTime getCreditDate() { return creditDate; }
}