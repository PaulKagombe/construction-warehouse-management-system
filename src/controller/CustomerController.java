package controller;

import database.CustomerDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;

public class CustomerController {

    private final CustomerDAO customerDAO = new CustomerDAO();

    public ObservableList<Customer> refreshCustomers() {
        return FXCollections.observableArrayList(customerDAO.getAllCustomers());
    }

    public boolean addCustomer(Customer customer) {
        return customerDAO.addCustomer(customer);
    }

    public boolean updateCustomer(Customer customer) {
        return customerDAO.updateCustomer(customer);
    }

    public boolean deleteCustomer(int customerId) {
        return customerDAO.deleteCustomer(customerId);
    }

    public Customer findById(int id) {
        return customerDAO.findCustomerById(id);
    }
}