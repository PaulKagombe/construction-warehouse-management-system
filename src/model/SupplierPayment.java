package model;

import java.time.LocalDateTime;

public class SupplierPayment {
    private int paymentId;
    private int creditId;
    private int supplierId;
    private String supplierName;
    private double amount;
    private String paymentMethod;
    private LocalDateTime paymentDate;

    public SupplierPayment(int paymentId, int creditId, int supplierId,
                           String supplierName, double amount,
                           String paymentMethod, LocalDateTime paymentDate) {
        this.paymentId = paymentId;
        this.creditId = creditId;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
    }

    public int getPaymentId() { return paymentId; }
    public int getCreditId() { return creditId; }
    public int getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
}