package controller;

import database.MaterialDAo;
import database.SupplierDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Material;

public class MaterialController {

    private final MaterialDAo materialDAO = new MaterialDAo();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    public ObservableList<Material> getAllMaterials() {
        return FXCollections.observableArrayList(materialDAO.getAllMaterials());
    }

    public boolean addMaterial(Material material) {
        return materialDAO.addMaterial(material);
    }

    public boolean updateMaterial(Material material) {
        return materialDAO.updateMaterial(material);
    }

    public boolean deleteMaterial(int materialId) {
        return materialDAO.deleteMaterialById(materialId);
    }

    public Material findByName(String name) {
        return materialDAO.findMaterialByName(name);
    }

    public boolean updateQuantity(int materialId, int newQty) {
        return materialDAO.updateMaterialQuantity(materialId, newQty);
    }

    public int getLowStockCount() {
        return materialDAO.getLowStockCount();
    }

    public double getTotalInventoryValue() {
        return materialDAO.getTotalInventoryValue();
    }

    public int getTotalMaterials() {
        return materialDAO.getAllMaterials().size();
    }

    public String getStockReport() {
        return materialDAO.getStockReportText();
    }

    public String getLowStockReport() {
        return materialDAO.getLowStockReportText();
    }

    public boolean supplierExists(int supplierId) {
        return supplierDAO.supplierExists(supplierId);
    }
}