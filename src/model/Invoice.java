package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Invoice {
    private int invoiceId;
    private int customerId;
    private String customerName;
    private int saleId;
    private double totalAmount;
    private double amountPaid;
    private double balance;
    private String status;
    private LocalDate dueDate;
    private LocalDateTime invoiceDate;

    public Invoice(int invoiceId, int customerId, String customerName,
                   int saleId, double totalAmount, double amountPaid,
                   double balance, String status, LocalDate dueDate,
                   LocalDateTime invoiceDate) {
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.saleId = saleId;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.balance = balance;
        this.status = status;
        this.dueDate = dueDate;
        this.invoiceDate = invoiceDate;
    }

    public int getInvoiceId() { return invoiceId; }
    public int getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public int getSaleId() { return saleId; }
    public double getTotalAmount() { return totalAmount; }
    public double getAmountPaid() { return amountPaid; }
    public double getBalance() { return balance; }
    public String getStatus() { return status; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDateTime getInvoiceDate() { return invoiceDate; }
}