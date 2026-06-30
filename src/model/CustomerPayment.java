package model;

import java.time.LocalDateTime;

public class CustomerPayment {
    private int paymentId;
    private int invoiceId;
    private int customerId;
    private String customerName;
    private double amount;
    private String paymentMethod;
    private LocalDateTime paymentDate;

    public CustomerPayment(int paymentId, int invoiceId, int customerId,
                           String customerName, double amount,
                           String paymentMethod, LocalDateTime paymentDate) {
        this.paymentId = paymentId;
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
    }

    public int getPaymentId() { return paymentId; }
    public int getInvoiceId() { return invoiceId; }
    public int getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
}