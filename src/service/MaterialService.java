package service;

import database.MaterialDAo;
import model.Material;

import java.util.List;

public class MaterialService implements BaseService<Material> {
    private MaterialDAo materialDAO;

    public MaterialService(MaterialDAo materialDAO) {
        this.materialDAO = materialDAO;
    }

    @Override
    public boolean add(Material material) {
        return materialDAO.addMaterial(material);
    }

    @Override
    public boolean update(Material material) {
        return materialDAO.updateMaterial(material);
    }

    @Override
    public boolean delete(int id) {
        return materialDAO.deleteMaterialById(id);
    }

    @Override
    public Material findById(int id) {
        // MaterialDAo doesn't have findById, assuming findMaterialByName is the closest
        // This needs to be addressed for a proper BaseService implementation
        return null; 
    }

    public Material findByName(String name) {
        return materialDAO.findMaterialByName(name);
    }

    @Override
    public List<Material> getAll() {
        return materialDAO.getAllMaterials();
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

    public String getStockReport() {
        return materialDAO.getStockReportText();
    }

    public String getLowStockReport() {
        return materialDAO.getLowStockReportText();
    }
}