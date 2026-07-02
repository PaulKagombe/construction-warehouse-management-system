package ui.forms;

import controller.SupplierController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Supplier;
import ui.StyledButton;
import util.AlertHelper;

public class SupplierForm extends Stage {

    private final TextField idField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField phoneField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField locationField = new TextField();

    private final SupplierController controller = new SupplierController();

    private final boolean editMode;

    public SupplierForm(Supplier supplier, Runnable onSaveSuccess) {

        editMode = (supplier != null);

        setTitle(editMode ? "Edit Supplier" : "Add Supplier");
        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);

        // Grid setup
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Labels
        grid.add(new Label("Supplier ID:"), 0, 0);
        grid.add(new Label("Supplier Name:"), 0, 1);
        grid.add(new Label("Phone Number:"), 0, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(new Label("Location:"), 0, 4);

        // Input fields
        idField.setPromptText("Enter Supplier ID");
        nameField.setPromptText("Enter Supplier Name");
        phoneField.setPromptText("Enter Phone Number");
        emailField.setPromptText("Enter Email Address");
        locationField.setPromptText("Enter Location");

        // Set column widths
        TextField[] fields = {idField, nameField, phoneField, emailField, locationField};
        for (TextField field : fields) {
            field.setPrefWidth(250);
            field.setStyle("-fx-font-size: 12;");
        }

        grid.add(idField, 1, 0);
        grid.add(nameField, 1, 1);
        grid.add(phoneField, 1, 2);
        grid.add(emailField, 1, 3);
        grid.add(locationField, 1, 4);

        // Pre-fill if editing
        if (editMode) {
            idField.setText(String.valueOf(supplier.getSupplierId()));
            idField.setDisable(true);
            nameField.setText(supplier.getSupplierName());
            phoneField.setText(supplier.getPhoneNumber());
            emailField.setText(supplier.getEmail());
            locationField.setText(supplier.getLocation());
        }

        // Buttons
        StyledButton saveButton = StyledButton.success(editMode ? "Update" : "Add");
        StyledButton cancelButton = StyledButton.secondary("Cancel");

        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        saveButton.setOnAction(e -> {
            if (!validateInput()) return;

            Supplier s = new Supplier(
                    Integer.parseInt(idField.getText()),
                    nameField.getText().trim(),
                    phoneField.getText().trim(),
                    emailField.getText().trim(),
                    locationField.getText().trim()
            );

            boolean success = editMode ? controller.updateSupplier(s) : controller.addSupplier(s);

            if (success) {
                AlertHelper.success(editMode ? "Supplier updated successfully." : "Supplier added successfully.");
                onSaveSuccess.run();
                close();
            } else {
                AlertHelper.error("Error", "Failed to save supplier.");
            }
        });

        cancelButton.setOnAction(e -> close());

        // Main layout
        VBox root = new VBox(15, grid, buttonBox);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f8f9fa;");

        Scene scene = new Scene(root);
        setScene(scene);
        setWidth(450);
        setHeight(350);
    }

    private boolean validateInput() {
        try {
            if (idField.getText().isEmpty() || nameField.getText().isEmpty()) {
                AlertHelper.warning("Validation", "ID and Name are required.");
                return false;
            }

            Integer.parseInt(idField.getText());

            return true;
        } catch (NumberFormatException e) {
            AlertHelper.error("Validation", "Please enter a valid Supplier ID.");
            return false;
        }
    }
}


