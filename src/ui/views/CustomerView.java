package ui.views;

import controller.CustomerController;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Customer;
import ui.forms.CustomerForm;
import ui.util.TableUtils;

public class CustomerView extends VBox {

    private final CustomerController controller;

    private final TableView<Customer> table;

    private final TextField searchField;

    public CustomerView() {

        controller = new CustomerController();

        setSpacing(15);
        setPadding(new Insets(20));

        // ==========================
        // Title
        // ==========================

        Label title = new Label("Customers");
        title.setStyle("-fx-font-size:22px;-fx-font-weight:bold;");

        // ==========================
        // Search
        // ==========================

        searchField = new TextField();
        searchField.setPromptText("Search customer...");

        // ==========================
        // Buttons
        // ==========================

        Button addButton = new Button("Add");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        Button refreshButton = new Button("Refresh");

        HBox toolbar = new HBox(
                10,
                addButton,
                editButton,
                deleteButton,
                refreshButton
        );

        toolbar.setAlignment(Pos.CENTER_LEFT);

        // ==========================
        // Table
        // ==========================

        table = new TableView<>();
        TableUtils.configureTableLayout(table);

        TableColumn<Customer, Integer> idCol =
                new TableColumn<>("ID");
        idCol.setCellValueFactory(
                new PropertyValueFactory<>("customerId")
        );
        idCol.setMinWidth(100);

        TableColumn<Customer, String> nameCol =
                new TableColumn<>("Customer Name");
        nameCol.setCellValueFactory(
                new PropertyValueFactory<>("customerName")
        );
        nameCol.setMinWidth(250);

        TableColumn<Customer, String> phoneCol =
                new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(
                new PropertyValueFactory<>("phoneNumber")
        );
        phoneCol.setMinWidth(180);

        table.getColumns().addAll(
                idCol,
                nameCol,
                phoneCol
        );

        VBox.setVgrow(table, Priority.ALWAYS);

        // Load data
        loadCustomers();

        // ==========================
        // Button Events
        // ==========================

        addButton.setOnAction(e -> {

            CustomerForm form =
                    new CustomerForm(
                            null,
                            this::loadCustomers
                    );

            form.showAndWait();

        });

        editButton.setOnAction(e -> {

            Customer selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {

                new Alert(
                        Alert.AlertType.WARNING,
                        "Please select a customer."
                ).showAndWait();

                return;
            }

            CustomerForm form =
                    new CustomerForm(
                            selected,
                            this::loadCustomers
                    );

            form.showAndWait();

        });

        deleteButton.setOnAction(e -> {

            Customer selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {

                new Alert(
                        Alert.AlertType.WARNING,
                        "Please select a customer."
                ).showAndWait();

                return;
            }

            Alert confirm =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirm.setHeaderText("Delete Customer");

            confirm.setContentText(
                    "Delete " +
                            selected.getCustomerName() +
                            "?"
            );

            confirm.showAndWait().ifPresent(result -> {

                if (result == ButtonType.OK) {

                    controller.deleteCustomer(
                            selected.getCustomerId()
                    );

                    loadCustomers();

                }

            });

        });

        refreshButton.setOnAction(e -> loadCustomers());

        // ==========================
        // Layout
        // ==========================

        getChildren().addAll(
                title,
                searchField,
                toolbar,
                table
        );
    }

    /**
     * Loads customers from the database
     */
    private void loadCustomers() {

        ObservableList<Customer> customers =
                controller.refreshCustomers();

        FilteredList<Customer> filtered =
                new FilteredList<>(customers, customer -> true);

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {

            String keyword =
                    newValue.toLowerCase().trim();

            filtered.setPredicate(customer -> {

                if (keyword.isEmpty()) {
                    return true;
                }

                return customer.getCustomerName()
                        .toLowerCase()
                        .contains(keyword)

                        ||

                        customer.getPhoneNumber()
                                .contains(keyword)

                        ||

                        String.valueOf(customer.getCustomerId())
                                .contains(keyword);

            });

        });

        SortedList<Customer> sorted =
                new SortedList<>(filtered);

        sorted.comparatorProperty()
                .bind(table.comparatorProperty());

        table.setItems(sorted);

    }

}