package ui;

import database.CustomerDAO;
import database.MaterialDAo;
import database.SalesDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Customer;
import model.Material;
import model.Sale;
import model.User;
import util.SessionManager;

public class SalesDashboard extends javafx.application.Application {

    private User loggedInUser;
    private MaterialDAo materialDAO = new MaterialDAo();
    private CustomerDAO customerDAO = new CustomerDAO();
    private SalesDAO salesDAO = new SalesDAO();

    public SalesDashboard(User user) {
        this.loggedInUser = user;
    }

    public SalesDashboard() {}

    @Override
    public void start(Stage stage) {

        // ===== TOP BAR =====
        Label titleLabel = new Label("WAREHOUSE MANAGEMENT SYSTEM");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.WHITE);

        Label userLabel = new Label("Logged in as: " + loggedInUser.getUsername() + " (SALES)");
        userLabel.setFont(Font.font("Arial", 12));
        userLabel.setTextFill(Color.LIGHTGRAY);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(
                "-fx-background-color: #e74c3c;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );
        logoutBtn.setOnAction(e -> {
            SessionManager.stopSession();
            stage.close();
            try { new LoginScreen().start(new Stage()); } catch (Exception ex) { ex.printStackTrace(); }
        });

        HBox topBar = new HBox();
        topBar.setStyle("-fx-background-color: #2c3e50;");
        topBar.setPadding(new Insets(12, 20, 12, 20));
        topBar.setSpacing(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(3, titleLabel, userLabel);
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        topBar.getChildren().addAll(titleBox, logoutBtn);

        // ===== SIDEBAR =====
        VBox sidebar = new VBox(5);
        sidebar.setPadding(new Insets(15, 10, 15, 10));
        sidebar.setStyle("-fx-background-color: #34495e;");
        sidebar.setPrefWidth(200);

        Label sidebarTitle = new Label("MENU");
        sidebarTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        sidebarTitle.setTextFill(Color.LIGHTGRAY);
        sidebarTitle.setPadding(new Insets(0, 0, 10, 5));

        String[] menuItems = {
                "Home", "View Materials", "Issue Material", "Customers", "Sales Report"
        };

        StackPane contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: #ecf0f1;");

        sidebar.getChildren().add(sidebarTitle);

        for (String item : menuItems) {
            Button btn = createSidebarButton(item);
            btn.setOnAction(e -> loadContent(item, contentArea));
            sidebar.getChildren().add(btn);
        }

        // ===== MAIN LAYOUT =====
        HBox body = new HBox(sidebar, contentArea);
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        VBox root = new VBox(topBar, body);
        VBox.setVgrow(body, Priority.ALWAYS);

        loadContent("Home", contentArea);

        Scene scene = new Scene(root, 1000, 650);
        stage.setTitle("Sales Dashboard");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        // -----------------------------------------------
// SESSION TIMEOUT — kick out after 10 min inactivity
// -----------------------------------------------
        SessionManager.setOnTimeout(() -> {
            SessionManager.stopSession();
            stage.close();
            try { new LoginScreen().start(new Stage()); } catch (Exception ex) { ex.printStackTrace(); }
        });
        SessionManager.startSession();

// Reset timer on any mouse or keyboard activity
        scene.setOnMouseMoved(e -> SessionManager.resetTimer());
        scene.setOnMouseClicked(e -> SessionManager.resetTimer());
        scene.setOnKeyPressed(e -> SessionManager.resetTimer());
    }

    private Button createSidebarButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(180);
        btn.setPrefHeight(40);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(0, 0, 0, 15));
        btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: #2c3e50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13px;" +
                        "-fx-cursor: hand;"
        ));
        return btn;
    }

    private void loadContent(String section, StackPane contentArea) {
        contentArea.getChildren().clear();
        switch (section) {
            case "Home":            contentArea.getChildren().add(buildHomePanel()); break;
            case "View Materials":  contentArea.getChildren().add(buildMaterialsPanel()); break;
            case "Issue Material":  contentArea.getChildren().add(buildIssueMaterialPanel()); break;
            case "Customers":       contentArea.getChildren().add(buildCustomersPanel()); break;
            case "Sales Report":    contentArea.getChildren().add(buildSalesReportPanel()); break;
        }
    }

    // ===================================================
    // HOME PANEL
    // ===================================================
    private VBox buildHomePanel() {

        Label heading = new Label("Sales Overview");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        heading.setPadding(new Insets(0, 0, 20, 0));

        int totalCustomers = customerDAO.getAllCustomers().size();
        int totalSales = salesDAO.getTotalSalesCount();
        double totalRevenue = salesDAO.getTotalRevenue();

        HBox row1 = new HBox(15,
                buildStatCard("Total Customers", String.valueOf(totalCustomers), "#27ae60"),
                buildStatCard("Total Sales", String.valueOf(totalSales), "#e67e22"),
                buildStatCard("Total Revenue",
                        "KES " + String.format("%.2f", totalRevenue), "#2980b9")
        );
        row1.setAlignment(Pos.CENTER);

        VBox panel = new VBox(15, heading, row1);
        panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color: #ecf0f1;");
        return panel;
    }

    // ===================================================
    // VIEW MATERIALS PANEL
    // ===================================================
    private VBox buildMaterialsPanel() {

        Label heading = new Label("Available Materials");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TableView<Material> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Material, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("materialId"));

        TableColumn<Material, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("materialName"));

        TableColumn<Material, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Material, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Material, Double> priceCol = new TableColumn<>("Unit Price (KES)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        TableColumn<Material, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unitOfMeasure"));
        table.getColumns().addAll(idCol, nameCol, categoryCol, qtyCol, priceCol, unitCol);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or category...");
        searchField.setPrefWidth(300);

        ObservableList<Material> allMaterials = getMaterials();
        table.setItems(allMaterials);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                table.setItems(allMaterials);
            } else {
                String lower = newVal.toLowerCase();
                ObservableList<Material> filtered = FXCollections.observableArrayList();
                for (Material m : allMaterials) {
                    if (m.getMaterialName().toLowerCase().contains(lower) ||
                            m.getCategory().toLowerCase().contains(lower)) {
                        filtered.add(m);
                    }
                }
                table.setItems(filtered);
            }
        });

        Button refreshBtn = actionButton("Refresh", "#2980b9");
        refreshBtn.setOnAction(e -> table.setItems(getMaterials()));

        HBox buttons = new HBox(10, refreshBtn);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        VBox panel = new VBox(10, heading, searchField, table, buttons);
        panel.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);
        return panel;
    }

    // ===================================================
    // ISSUE MATERIAL PANEL
    // ===================================================
    private VBox buildIssueMaterialPanel() {

        Label heading = new Label("Issue Material");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TextField customerIdField = new TextField();
        customerIdField.setPromptText("Customer ID");
        customerIdField.setMaxWidth(300);

        TextField materialNameField = new TextField();
        materialNameField.setPromptText("Material Name");
        materialNameField.setMaxWidth(300);

        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        quantityField.setMaxWidth(300);

        Label msgLabel = new Label("");
        msgLabel.setFont(Font.font("Arial", 13));

        Button issueBtn = actionButton("Issue Material", "#27ae60");
        Button clearBtn = actionButton("Clear", "#7f8c8d");

        issueBtn.setOnAction(e -> {
            String customerIdText = customerIdField.getText().trim();
            String materialName = materialNameField.getText().trim();
            String qtyText = quantityField.getText().trim();

            if (customerIdText.isEmpty() || materialName.isEmpty() || qtyText.isEmpty()) {
                msgLabel.setTextFill(Color.RED);
                msgLabel.setText("Please fill all fields.");
                return;
            }

            try {
                int customerId = Integer.parseInt(customerIdText);
                int quantity = Integer.parseInt(qtyText);

                Customer customer = customerDAO.findCustomerById(customerId);
                if (customer == null) {
                    msgLabel.setTextFill(Color.RED);
                    msgLabel.setText("Customer not found.");
                    return;
                }

                Material material = materialDAO.findMaterialByName(materialName);
                if (material == null) {
                    msgLabel.setTextFill(Color.RED);
                    msgLabel.setText("Material not found.");
                    return;
                }

                if (quantity <= 0) {
                    msgLabel.setTextFill(Color.RED);
                    msgLabel.setText("Quantity must be greater than zero.");
                    return;
                }

                if (quantity > material.getQuantity()) {
                    msgLabel.setTextFill(Color.RED);
                    msgLabel.setText("Insufficient stock. Available: " + material.getQuantity());
                    return;
                }

                int newQty = material.getQuantity() - quantity;
                materialDAO.updateMaterialQuantity(material.getMaterialId(), newQty);

                Sale sale = new Sale(
                        0, customerId, material.getMaterialId(),
                        material.getMaterialName(), quantity, material.getUnitPrice()
                );

                if (salesDAO.addSale(sale)) {
                    int realSaleId = salesDAO.getLastSaleId();
                    Sale saleWithId = new Sale(
                            realSaleId,
                            sale.getCustomerId(),
                            sale.getMaterialId(),
                            sale.getMaterialName(),
                            sale.getQuantity(),
                            sale.getUnitPrice()
                    );
                    customerIdField.clear();
                    materialNameField.clear();
                    quantityField.clear();
                    msgLabel.setText("");
                    showReceipt(customer, material, quantity, saleWithId, newQty);
                } else {
                    msgLabel.setTextFill(Color.RED);
                    msgLabel.setText("Failed to record sale.");
                }

            } catch (NumberFormatException ex) {
                msgLabel.setTextFill(Color.RED);
                msgLabel.setText("Please enter valid numbers.");
            }
        });

        clearBtn.setOnAction(e -> {
            customerIdField.clear();
            materialNameField.clear();
            quantityField.clear();
            msgLabel.setText("");
        });

        VBox panel = new VBox(12,
                heading,
                new Label("Customer ID:"), customerIdField,
                new Label("Material Name:"), materialNameField,
                new Label("Quantity:"), quantityField,
                new HBox(10, issueBtn, clearBtn),
                msgLabel
        );
        panel.setPadding(new Insets(30));
        return panel;
    }

    // ===================================================
    // CUSTOMERS PANEL
    // ===================================================
    private VBox buildCustomersPanel() {

        Label heading = new Label("Customers");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TableView<Customer> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Customer, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        table.getColumns().addAll(idCol, nameCol, phoneCol);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or phone...");
        searchField.setPrefWidth(300);

        ObservableList<Customer> allCustomers = getCustomers();
        table.setItems(allCustomers);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                table.setItems(allCustomers);
            } else {
                String lower = newVal.toLowerCase();
                ObservableList<Customer> filtered = FXCollections.observableArrayList();
                for (Customer c : allCustomers) {
                    if (c.getCustomerName().toLowerCase().contains(lower) ||
                            c.getPhoneNumber().toLowerCase().contains(lower)) {
                        filtered.add(c);
                    }
                }
                table.setItems(filtered);
            }
        });
        Button addBtn = actionButton("Add Customer", "#27ae60");
        Button editBtn = actionButton("Edit Selected", "#e67e22");
        Button deleteBtn = actionButton("Delete Selected", "#e74c3c");
        Button refreshBtn = actionButton("Refresh", "#2980b9");

        addBtn.setOnAction(e -> showAddCustomerDialog(table));
        editBtn.setOnAction(e -> {
            Customer selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Please select a customer to edit.");
                return;
            }
            showEditCustomerDialog(table, selected);
        });
        deleteBtn.setOnAction(e -> {
            Customer selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Please select a customer to delete.");
                return;
            }
            if (confirmDialog("Delete customer '" + selected.getCustomerName() + "'?")) {
                customerDAO.deleteCustomer(selected.getCustomerId());
                table.setItems(getCustomers());
            }
        });
        refreshBtn.setOnAction(e -> table.setItems(getCustomers()));

        HBox buttons = new HBox(10, addBtn, editBtn, deleteBtn, refreshBtn);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        VBox panel = new VBox(10, heading, searchField, table, buttons);
        panel.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);
        return panel;
    }

    private void showAddCustomerDialog(TableView<Customer> table) {

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add Customer");

        TextField idField = new TextField(); idField.setPromptText("Customer ID");
        TextField nameField = new TextField(); nameField.setPromptText("Customer Name");
        TextField phoneField = new TextField(); phoneField.setPromptText("Phone Number");

        Label msgLabel = new Label("");
        msgLabel.setTextFill(Color.RED);

        Button saveBtn = actionButton("Save", "#27ae60");
        Button cancelBtn = actionButton("Cancel", "#e74c3c");

        saveBtn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();

                if (name.isEmpty() || phone.isEmpty()) {
                    msgLabel.setText("Please fill all fields.");
                    return;
                }

                Customer customer = new Customer(id, name, phone);
                if (customerDAO.addCustomer(customer)) {
                    table.setItems(getCustomers());
                    dialog.close();
                } else {
                    msgLabel.setText("Failed to save customer.");
                }
            } catch (NumberFormatException ex) {
                msgLabel.setText("Please enter a valid Customer ID.");
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));
        form.addRow(0, new Label("Customer ID:"), idField);
        form.addRow(1, new Label("Name:"), nameField);
        form.addRow(2, new Label("Phone:"), phoneField);
        form.add(msgLabel, 0, 3, 2, 1);
        form.add(new HBox(10, saveBtn, cancelBtn), 0, 4, 2, 1);

        dialog.setScene(new Scene(form, 320, 220));
        dialog.showAndWait();
    }

    // ===================================================
    // SALES REPORT PANEL
    // ===================================================
    private VBox buildSalesReportPanel() {

        Label heading = new Label("Sales Report");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        TableView<Sale> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Sale, Integer> idCol = new TableColumn<>("Sale ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("salesId"));

        TableColumn<Sale, Integer> customerCol = new TableColumn<>("Customer ID");
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn<Sale, String> materialCol = new TableColumn<>("Material");
        materialCol.setCellValueFactory(new PropertyValueFactory<>("materialName"));

        TableColumn<Sale, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Sale, Double> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        TableColumn<Sale, Double> totalCol = new TableColumn<>("Total (KES)");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        table.getColumns().addAll(idCol, customerCol, materialCol, qtyCol, priceCol, totalCol);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by material name...");
        searchField.setPrefWidth(300);

        ObservableList<Sale> allSales = getSales();
        table.setItems(allSales);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                table.setItems(allSales);
            } else {
                String lower = newVal.toLowerCase();
                ObservableList<Sale> filtered = FXCollections.observableArrayList();
                for (Sale s : allSales) {
                    if (s.getMaterialName().toLowerCase().contains(lower)) {
                        filtered.add(s);
                    }
                }
                table.setItems(filtered);
            }
        });

        Button refreshBtn = actionButton("Refresh", "#2980b9");
        refreshBtn.setOnAction(e -> table.setItems(getSales()));

        HBox buttons = new HBox(10, refreshBtn);
        buttons.setPadding(new Insets(10, 0, 0, 0));
        VBox panel = new VBox(10, heading, searchField, table, buttons);
        panel.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);
        return panel;
    }

    // ===================================================
    // RECEIPT
    // ===================================================
    private void showReceipt(Customer customer, Material material,
                             int quantity, Sale sale, int remainingStock) {

        Stage receiptStage = new Stage();
        receiptStage.initModality(Modality.APPLICATION_MODAL);
        receiptStage.setTitle("Sale Receipt");

        String dateTime = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        Label shopName = new Label("CONSTRUCTION WAREHOUSE");
        shopName.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        shopName.setAlignment(Pos.CENTER);

        Label shopSubtitle = new Label("Materials & Hardware Store");
        shopSubtitle.setFont(Font.font("Arial", 12));
        shopSubtitle.setTextFill(Color.GRAY);

        Label divider1 = new Label("================================================");
        divider1.setFont(Font.font("Monospaced", 11));

        Label receiptTitle = new Label("OFFICIAL RECEIPT");
        receiptTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label divider2 = new Label("================================================");
        divider2.setFont(Font.font("Monospaced", 11));

        Label dateLabel = new Label("Date       : " + dateTime);
        dateLabel.setFont(Font.font("Monospaced", 12));

        Label saleIdLabel = new Label("Sale ID    : " + sale.getSalesId());
        saleIdLabel.setFont(Font.font("Monospaced", 12));

        Label divider3 = new Label("------------------------------------------------");
        divider3.setFont(Font.font("Monospaced", 11));

        Label customerHeading = new Label("CUSTOMER DETAILS");
        customerHeading.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Label customerIdLabel = new Label("Customer ID: " + customer.getCustomerId());
        customerIdLabel.setFont(Font.font("Monospaced", 12));

        Label customerNameLabel = new Label("Name       : " + customer.getCustomerName());
        customerNameLabel.setFont(Font.font("Monospaced", 12));

        Label customerPhoneLabel = new Label("Phone      : " + customer.getPhoneNumber());
        customerPhoneLabel.setFont(Font.font("Monospaced", 12));

        Label divider4 = new Label("------------------------------------------------");
        divider4.setFont(Font.font("Monospaced", 11));

        Label itemHeading = new Label("ITEM DETAILS");
        itemHeading.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Label materialLabel = new Label("Material   : " + material.getMaterialName());
        materialLabel.setFont(Font.font("Monospaced", 12));

        Label categoryLabel = new Label("Category   : " + material.getCategory());
        categoryLabel.setFont(Font.font("Monospaced", 12));

        Label unitLabel = new Label("Unit       : " + material.getUnitOfMeasure());
        unitLabel.setFont(Font.font("Monospaced", 12));

        Label quantityLabel = new Label("Quantity   : " + quantity);
        quantityLabel.setFont(Font.font("Monospaced", 12));

        Label unitPriceLabel = new Label("Unit Price : KES " + String.format("%.2f", material.getUnitPrice()));
        unitPriceLabel.setFont(Font.font("Monospaced", 12));

        Label divider5 = new Label("------------------------------------------------");
        divider5.setFont(Font.font("Monospaced", 11));

        Label totalLabel = new Label("TOTAL      : KES " + String.format("%.2f", sale.getTotalAmount()));
        totalLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 14));
        totalLabel.setTextFill(Color.web("#27ae60"));

        Label divider6 = new Label("================================================");
        divider6.setFont(Font.font("Monospaced", 11));

        Label thankYou = new Label("Thank you for your business!");
        thankYou.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        thankYou.setTextFill(Color.GRAY);

        Button printBtn = new Button("🖨  Print Receipt");
        printBtn.setStyle(
                "-fx-background-color: #2980b9;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-cursor: hand;"
        );

        Button closeBtn = new Button("Close");
        closeBtn.setStyle(
                "-fx-background-color: #7f8c8d;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-cursor: hand;"
        );

        printBtn.setOnAction(e -> {
            javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(receiptStage)) {
                VBox printContent = buildPrintableReceipt(
                        customer, material, quantity, sale, remainingStock, dateTime
                );
                boolean success = job.printPage(printContent);
                if (success) {
                    job.endJob();
                    showSuccessAlert("Receipt printed successfully!");
                }
            }
        });

        closeBtn.setOnAction(e -> receiptStage.close());

        HBox buttonBox = new HBox(10, printBtn, closeBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        VBox receiptBox = new VBox(6,
                shopName, shopSubtitle,
                divider1, receiptTitle, divider2,
                dateLabel, saleIdLabel,
                divider3,
                customerHeading,
                customerIdLabel, customerNameLabel, customerPhoneLabel,
                divider4,
                itemHeading,
                materialLabel, categoryLabel, unitLabel, quantityLabel, unitPriceLabel,
                divider5,
                totalLabel,
                divider6,
                thankYou,
                buttonBox
        );
        receiptBox.setPadding(new Insets(25));
        receiptBox.setAlignment(Pos.CENTER_LEFT);
        receiptBox.setStyle("-fx-background-color: white;");

        ScrollPane scrollPane = new ScrollPane(receiptBox);
        scrollPane.setFitToWidth(true);

        Scene receiptScene = new Scene(scrollPane, 480, 600);
        receiptStage.setScene(receiptScene);
        receiptStage.setResizable(false);
        receiptStage.show();
    }

    private VBox buildPrintableReceipt(
            Customer customer, Material material,
            int quantity, Sale sale, int remainingStock, String dateTime
    ) {
        Label shopName = new Label("CONSTRUCTION WAREHOUSE");
        shopName.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label line1 = new Label("================================================");
        line1.setFont(Font.font("Monospaced", 10));

        Label title = new Label("OFFICIAL RECEIPT");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        Label line2 = new Label("================================================");
        line2.setFont(Font.font("Monospaced", 10));

        Label date = new Label("Date       : " + dateTime);
        date.setFont(Font.font("Monospaced", 11));

        Label saleId = new Label("Sale ID    : " + sale.getSalesId());
        saleId.setFont(Font.font("Monospaced", 11));

        Label line3 = new Label("------------------------------------------------");
        line3.setFont(Font.font("Monospaced", 10));

        Label custHeading = new Label("CUSTOMER DETAILS");
        custHeading.setFont(Font.font("Arial", FontWeight.BOLD, 11));

        Label custId = new Label("Customer ID: " + customer.getCustomerId());
        custId.setFont(Font.font("Monospaced", 11));

        Label custName = new Label("Name       : " + customer.getCustomerName());
        custName.setFont(Font.font("Monospaced", 11));

        Label custPhone = new Label("Phone      : " + customer.getPhoneNumber());
        custPhone.setFont(Font.font("Monospaced", 11));

        Label line4 = new Label("------------------------------------------------");
        line4.setFont(Font.font("Monospaced", 10));

        Label itemHeading = new Label("ITEM DETAILS");
        itemHeading.setFont(Font.font("Arial", FontWeight.BOLD, 11));

        Label mat = new Label("Material   : " + material.getMaterialName());
        mat.setFont(Font.font("Monospaced", 11));

        Label cat = new Label("Category   : " + material.getCategory());
        cat.setFont(Font.font("Monospaced", 11));

        Label unit = new Label("Unit       : " + material.getUnitOfMeasure());
        unit.setFont(Font.font("Monospaced", 11));

        Label qty = new Label("Quantity   : " + quantity);
        qty.setFont(Font.font("Monospaced", 11));

        Label price = new Label("Unit Price : KES " + String.format("%.2f", material.getUnitPrice()));
        price.setFont(Font.font("Monospaced", 11));

        Label line5 = new Label("------------------------------------------------");
        line5.setFont(Font.font("Monospaced", 10));

        Label total = new Label("TOTAL      : KES " + String.format("%.2f", sale.getTotalAmount()));
        total.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));

        Label line6 = new Label("================================================");
        line6.setFont(Font.font("Monospaced", 10));

        Label thanks = new Label("Thank you for your business!");
        thanks.setFont(Font.font("Arial", 11));

        VBox content = new VBox(5,
                shopName, line1, title, line2,
                date, saleId, line3,
                custHeading, custId, custName, custPhone, line4,
                itemHeading, mat, cat, unit, qty, price, line5,
                total, line6, thanks
        );
        content.setPadding(new Insets(20));
        return content;
    }

    // ===================================================
    // DATA LOADERS
    // ===================================================
    private ObservableList<Material> getMaterials() {
        return FXCollections.observableArrayList(materialDAO.getAllMaterials());
    }

    private ObservableList<Customer> getCustomers() {
        return FXCollections.observableArrayList(customerDAO.getAllCustomers());
    }

    private ObservableList<Sale> getSales() {
        return FXCollections.observableArrayList(salesDAO.getAllSales());
    }

    // ===================================================
    // HELPERS
    // ===================================================
    private Button actionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8 16 8 16;"
        );
        return btn;
    }

    private VBox buildStatCard(String title, String value, String color) {
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, value.length() > 10 ? 18 : 28));
        valueLabel.setTextFill(Color.WHITE);
        valueLabel.setWrapText(true);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 13));
        titleLabel.setTextFill(Color.WHITE);

        VBox card = new VBox(5, valueLabel, titleLabel);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(220, 100);
        card.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 15;"
        );
        return card;
    }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }

    private boolean confirmDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.showAndWait();
        return alert.getResult() == ButtonType.YES;
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    private void showEditCustomerDialog(TableView<Customer> table, Customer customer) {

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Customer");

        TextField nameField = new TextField(customer.getCustomerName());
        TextField phoneField = new TextField(customer.getPhoneNumber());

        Label msgLabel = new Label("");
        msgLabel.setTextFill(Color.RED);

        Button saveBtn = actionButton("Save Changes", "#27ae60");
        Button cancelBtn = actionButton("Cancel", "#e74c3c");

        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                msgLabel.setText("Please fill all fields.");
                return;
            }

            Customer updated = new Customer(
                    customer.getCustomerId(), name, phone
            );

            if (customerDAO.updateCustomer(updated)) {
                table.setItems(getCustomers());
                dialog.close();
            } else {
                msgLabel.setText("Failed to update customer.");
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));
        form.addRow(0, new Label("Customer ID:"), new Label(String.valueOf(customer.getCustomerId())));
        form.addRow(1, new Label("Name:"), nameField);
        form.addRow(2, new Label("Phone:"), phoneField);
        form.add(msgLabel, 0, 3, 2, 1);
        form.add(new HBox(10, saveBtn, cancelBtn), 0, 4, 2, 1);

        dialog.setScene(new Scene(form, 320, 220));
        dialog.showAndWait();
    }

}