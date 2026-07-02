package ui.forms;

import controller.SalesController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Sale;
import ui.StyledButton;
import util.AlertHelper;

public class SalesForm extends Stage {

    private final TextField saleIdField = new TextField();
    private final TextField customerIdField = new TextField();
    private final TextField materialIdField = new TextField();
    private final TextField materialNameField = new TextField();
    private final TextField quantityField = new TextField();
    private final TextField unitPriceField = new TextField();
    private final TextField totalAmountField = new TextField();

    private final SalesController controller = new SalesController();

    private final boolean editMode;

    public SalesForm(Sale sale, Runnable onSaveSuccess) {

        editMode = (sale != null);

        setTitle(editMode ? "Edit Sale" : "Record Sale");
        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);

        // Grid setup
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Labels
        if (editMode) {
            grid.add(new Label("Sale ID:"), 0, 0);
        }

        grid.add(new Label("Customer ID:"), 0, editMode ? 1 : 0);
        grid.add(new Label("Material ID:"), 0, editMode ? 2 : 1);
        grid.add(new Label("Material Name:"), 0, editMode ? 3 : 2);
        grid.add(new Label("Quantity:"), 0, editMode ? 4 : 3);
        grid.add(new Label("Unit Price:"), 0, editMode ? 5 : 4);
        grid.add(new Label("Total Amount:"), 0, editMode ? 6 : 5);

        // Input fields
        if (editMode) {
            saleIdField.setPromptText("Sale ID");
            saleIdField.setPrefWidth(250);
            saleIdField.setStyle("-fx-font-size: 12;");
            saleIdField.setDisable(true);
            grid.add(saleIdField, 1, 0);
        }

        customerIdField.setPromptText("Enter Customer ID");
        materialIdField.setPromptText("Enter Material ID");
        materialNameField.setPromptText("Enter Material Name");
        quantityField.setPromptText("Enter Quantity");
        unitPriceField.setPromptText("Enter Unit Price");
        totalAmountField.setPromptText("Auto-calculated");
        totalAmountField.setDisable(true);

        // Set column widths
        TextField[] fields = {customerIdField, materialIdField, materialNameField, quantityField, unitPriceField, totalAmountField};
        for (TextField field : fields) {
            field.setPrefWidth(250);
            field.setStyle("-fx-font-size: 12;");
        }

        grid.add(customerIdField, 1, editMode ? 1 : 0);
        grid.add(materialIdField, 1, editMode ? 2 : 1);
        grid.add(materialNameField, 1, editMode ? 3 : 2);
        grid.add(quantityField, 1, editMode ? 4 : 3);
        grid.add(unitPriceField, 1, editMode ? 5 : 4);
        grid.add(totalAmountField, 1, editMode ? 6 : 5);

        // Pre-fill if editing
        if (editMode) {
            saleIdField.setText(String.valueOf(sale.getSalesId()));
            customerIdField.setText(String.valueOf(sale.getCustomerId()));
            materialIdField.setText(String.valueOf(sale.getMaterialId()));
            materialNameField.setText(sale.getMaterialName());
            quantityField.setText(String.valueOf(sale.getQuantity()));
            unitPriceField.setText(String.valueOf(sale.getUnitPrice()));
            double total = sale.getQuantity() * sale.getUnitPrice();
            totalAmountField.setText(String.format("%.2f", total));
        }

        // Auto-calculate total when quantity or price changes
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
        unitPriceField.textProperty().addListener((obs, oldVal, newVal) -> calculateTotal());

        // Buttons
        StyledButton saveButton = StyledButton.success(editMode ? "Update" : "Record Sale");
        StyledButton cancelButton = StyledButton.secondary("Cancel");

        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        saveButton.setOnAction(e -> {
            if (!validateInput()) return;

            // Use issueMaterial which handles the sale logic
            int customerId = Integer.parseInt(customerIdField.getText());
            String materialName = materialNameField.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText());

            String result = controller.issueMaterial(customerId, materialName, quantity);
            boolean success = (result == null);

            if (success) {
                AlertHelper.success("Sale recorded successfully.");
                onSaveSuccess.run();
                close();
            } else {
                AlertHelper.error("Error", result != null ? result : "Failed to record sale.");
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
        setHeight(editMode ? 420 : 380);
    }

    private void calculateTotal() {
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            double unitPrice = Double.parseDouble(unitPriceField.getText());
            double total = quantity * unitPrice;
            totalAmountField.setText(String.format("%.2f", total));
        } catch (NumberFormatException ignored) {
            totalAmountField.setText("");
        }
    }

    private boolean validateInput() {
        try {
            if (customerIdField.getText().isEmpty() || materialIdField.getText().isEmpty() || materialNameField.getText().isEmpty()) {
                AlertHelper.warning("Validation", "All fields are required.");
                return false;
            }

            Integer.parseInt(customerIdField.getText());
            Integer.parseInt(materialIdField.getText());
            Integer.parseInt(quantityField.getText());
            Double.parseDouble(unitPriceField.getText());

            return true;
        } catch (NumberFormatException e) {
            AlertHelper.error("Validation", "Please enter valid numbers for numeric fields.");
            return false;
        }
    }
}


