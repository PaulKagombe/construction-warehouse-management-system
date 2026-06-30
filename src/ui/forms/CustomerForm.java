package ui.forms;

import controller.CustomerController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Customer;

public class CustomerForm extends Stage {

    private final TextField idField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField phoneField = new TextField();

    private final CustomerController controller = new CustomerController();

    private final boolean editMode;

    public CustomerForm(Customer customer, Runnable onSaveSuccess) {

        editMode = (customer != null);

        setTitle(editMode ? "Edit Customer" : "Add Customer");

        initModality(Modality.APPLICATION_MODAL);

        Label idLabel = new Label("Customer ID:");
        Label nameLabel = new Label("Customer Name:");
        Label phoneLabel = new Label("Phone Number:");

        idField.setPromptText("Enter Customer ID");
        nameField.setPromptText("Enter Customer Name");
        phoneField.setPromptText("Enter Phone Number");

        if (editMode) {

            idField.setText(String.valueOf(customer.getCustomerId()));
            idField.setDisable(true);

            nameField.setText(customer.getCustomerName());
            phoneField.setText(customer.getPhoneNumber());

        }

        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        saveButton.setPrefWidth(100);
        cancelButton.setPrefWidth(100);

        saveButton.setOnAction(e -> {

            if (!validateInput()) {
                return;
            }

            Customer c = new Customer(

                    Integer.parseInt(idField.getText()),

                    nameField.getText().trim(),

                    phoneField.getText().trim()

            );

            boolean success;

            if (editMode) {

                success = controller.updateCustomer(c);

            } else {

                success = controller.addCustomer(c);

            }

            if (success) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Customer saved successfully.");
                alert.showAndWait();

                onSaveSuccess.run();

                close();

            } else {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Operation failed.");
                alert.showAndWait();

            }

        });

        cancelButton.setOnAction(e -> close());

        GridPane grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(15);

        grid.add(idLabel,0,0);
        grid.add(idField,1,0);

        grid.add(nameLabel,0,1);
        grid.add(nameField,1,1);

        grid.add(phoneLabel,0,2);
        grid.add(phoneField,1,2);

        HBox buttons = new HBox(10, saveButton, cancelButton);

        buttons.setAlignment(Pos.CENTER_RIGHT);

        grid.add(buttons,1,3);

        grid.setPadding(new Insets(20));

        Scene scene = new Scene(grid,420,240);

        setScene(scene);

    }

    private boolean validateInput() {

        if (idField.getText().isBlank()
                || nameField.getText().isBlank()
                || phoneField.getText().isBlank()) {

            new Alert(Alert.AlertType.WARNING,
                    "All fields are required.")
                    .showAndWait();

            return false;

        }

        try {

            Integer.parseInt(idField.getText());

        } catch (NumberFormatException ex) {

            new Alert(Alert.AlertType.WARNING,
                    "Customer ID must be numeric.")
                    .showAndWait();

            return false;

        }

        return true;

    }

}