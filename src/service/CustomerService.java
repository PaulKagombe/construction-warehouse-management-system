package service;

import database.CustomerDAO;
import model.Customer;

import java.util.List;

public class CustomerService implements BaseService<Customer> {
    private CustomerDAO customerDAO;

    public CustomerService(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    @Override
    public boolean add(Customer customer) {
        // Additional business logic or validation can be added here before calling DAO
        return customerDAO.addCustomer(customer);
    }

    @Override
    public boolean update(Customer customer) {
        // Additional business logic or validation
        return customerDAO.updateCustomer(customer);
    }

    @Override
    public boolean delete(int id) {
        // Additional business logic or validation
        return customerDAO.deleteCustomer(id);
    }

    @Override
    public Customer findById(int id) {
        return customerDAO.findCustomerById(id);
    }

    @Override
    public List<Customer> getAll() {
        return customerDAO.getAllCustomers();
    }
}