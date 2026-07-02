package ui.forms;

import controller.MaterialController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Material;
import ui.StyledButton;
import util.AlertHelper;

public class MaterialForm extends Stage {

    private final TextField idField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField categoryField = new TextField();
    private final TextField quantityField = new TextField();
    private final TextField unitPriceField = new TextField();
    private final TextField unitField = new TextField();
    private final TextField minimumStockField = new TextField();
    private final TextField supplierIdField = new TextField();

    private final MaterialController controller = new MaterialController();

    private final boolean editMode;

    public MaterialForm(Material material, Runnable onSaveSuccess) {

        editMode = (material != null);

        setTitle(editMode ? "Edit Material" : "Add Material");
        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);

        // Grid setup
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Labels
        grid.add(new Label("Material ID:"), 0, 0);
        grid.add(new Label("Material Name:"), 0, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(new Label("Quantity:"), 0, 3);
        grid.add(new Label("Unit Price:"), 0, 4);
        grid.add(new Label("Unit of Measure:"), 0, 5);
        grid.add(new Label("Minimum Stock:"), 0, 6);
        grid.add(new Label("Supplier ID:"), 0, 7);

        // Input fields
        idField.setPromptText("Enter Material ID");
        nameField.setPromptText("Enter Material Name");
        categoryField.setPromptText("e.g., Cement, Bricks");
        quantityField.setPromptText("0");
        unitPriceField.setPromptText("0.00");
        unitField.setPromptText("e.g., Bags, Units");
        minimumStockField.setPromptText("0");
        supplierIdField.setPromptText("Enter Supplier ID");

        // Set column widths
        TextField[] fields = {idField, nameField, categoryField, quantityField, unitPriceField, unitField, minimumStockField, supplierIdField};
        for (TextField field : fields) {
            field.setPrefWidth(250);
            field.setStyle("-fx-font-size: 12;");
        }

        grid.add(idField, 1, 0);
        grid.add(nameField, 1, 1);
        grid.add(categoryField, 1, 2);
        grid.add(quantityField, 1, 3);
        grid.add(unitPriceField, 1, 4);
        grid.add(unitField, 1, 5);
        grid.add(minimumStockField, 1, 6);
        grid.add(supplierIdField, 1, 7);

        // Pre-fill if editing
        if (editMode) {
            idField.setText(String.valueOf(material.getMaterialId()));
            idField.setDisable(true);
            nameField.setText(material.getMaterialName());
            categoryField.setText(material.getCategory());
            quantityField.setText(String.valueOf(material.getQuantity()));
            unitPriceField.setText(String.valueOf(material.getUnitPrice()));
            unitField.setText(material.getUnitOfMeasure());
            minimumStockField.setText(String.valueOf(material.getMinimumStockLevel()));
            supplierIdField.setText(String.valueOf(material.getSupplierId()));
        }

        // Buttons
        StyledButton saveButton = StyledButton.success(editMode ? "Update" : "Add");
        StyledButton cancelButton = StyledButton.secondary("Cancel");

        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        saveButton.setOnAction(e -> {
            if (!validateInput()) return;

            Material m = new Material(
                    Integer.parseInt(idField.getText()),
                    nameField.getText().trim(),
                    categoryField.getText().trim(),
                    Integer.parseInt(quantityField.getText()),
                    Double.parseDouble(unitPriceField.getText()),
                    unitField.getText().trim(),
                    Integer.parseInt(minimumStockField.getText()),
                    Integer.parseInt(supplierIdField.getText())
            );

            boolean success = editMode ? controller.updateMaterial(m) : controller.addMaterial(m);

            if (success) {
                AlertHelper.success(editMode ? "Material updated successfully." : "Material added successfully.");
                onSaveSuccess.run();
                close();
            } else {
                AlertHelper.error("Error", "Failed to save material.");
            }
        });

        cancelButton.setOnAction(e -> close());

        // Main layout
        VBox root = new VBox(15, grid, buttonBox);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8f9fa;");

        Scene scene = new Scene(root);
        setScene(scene);
        setWidth(500);
        setHeight(450);
    }

    private boolean validateInput() {
        try {
            if (idField.getText().isEmpty() || nameField.getText().isEmpty()) {
                AlertHelper.warning("Validation", "ID and Name are required.");
                return false;
            }

            Integer.parseInt(idField.getText());
            Integer.parseInt(quantityField.getText());
            Double.parseDouble(unitPriceField.getText());
            Integer.parseInt(minimumStockField.getText());
            Integer.parseInt(supplierIdField.getText());

            return true;
        } catch (NumberFormatException e) {
            AlertHelper.error("Validation", "Please enter valid numbers for numeric fields.");
            return false;
        }
    }
}


