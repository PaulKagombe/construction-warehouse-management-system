package service;

import database.SupplierDAO;
import model.Supplier;

import java.util.List;

public class SupplierService implements BaseService<Supplier> {
    private SupplierDAO supplierDAO;

    public SupplierService(SupplierDAO supplierDAO) {
        this.supplierDAO = supplierDAO;
    }

    @Override
    public boolean add(Supplier supplier) {
        return supplierDAO.addSupplier(supplier);
    }

    @Override
    public boolean update(Supplier supplier) {
        return supplierDAO.updateSupplier(supplier);
    }

    @Override
    public boolean delete(int id) {
        return supplierDAO.deleteSupplier(id);
    }

    @Override
    public Supplier findById(int id) {
        // SupplierDAO does not have findById directly, needs implementation
        return null; 
    }

    @Override
    public List<Supplier> getAll() {
        return supplierDAO.getAllSuppliers();
    }

    public boolean supplierExists(int supplierId) {
        return supplierDAO.supplierExists(supplierId);
    }
}