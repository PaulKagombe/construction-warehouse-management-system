package controller;

import database.CustomerDAO;
import database.SalesDAO;

public class DashboardController {

    private final SalesDAO salesDAO;
    private final CustomerDAO customerDAO;

    public DashboardController() {

        salesDAO = new SalesDAO();
        customerDAO = new CustomerDAO();

    }

    public int getTotalCustomers() {
        return customerDAO.getTotalCustomers();
    }

    public int getTotalSales() {
        return salesDAO.getTotalSalesCount();
    }

    public double getRevenue() {
        return salesDAO.getTotalRevenue();
    }

    public int getTodaySales() {
        return salesDAO.getTodaySalesCount();
    }

    public double getTodayRevenue() {
        return salesDAO.getTodayRevenue();
    }
}