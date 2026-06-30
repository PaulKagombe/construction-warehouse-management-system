package ui;

import controller.SalesController;
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
import model.*;
import ui.components.DashboardCard;
import ui.util.TableUtils;
import ui.views.CustomerView;
import ui.views.DashboardView;
import util.AlertHelper;
import util.SessionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SalesDashboardV2 extends BaseDashboard {

    private final SalesController controller = new SalesController();

    public SalesDashboardV2(User user) {
        super(user);
    }

    @Override
    protected void initialize() {

        topBar.setTitle("Sales Dashboard");
        sidebar.setActive("Dashboard");

        // Wire sidebar menu
        sidebar.setMenuSelectionListener(menu -> {
            switch (menu) {

                case "Dashboard" -> {
                    topBar.setTitle("Dashboard");
                    contentPane.getChildren().setAll(new DashboardView());
                }

                case "Inventory" -> {
                    topBar.setTitle("Available Materials");
                    contentPane.getChildren().setAll(buildMaterialsPanel());
                }

                case "Sales" -> {
                    topBar.setTitle("Issue Material / Sales");
                    contentPane.getChildren().setAll(buildSalesPanel());
                }

                case "Customers" -> {
                    topBar.setTitle("Customers");
                    contentPane.getChildren().setAll(new CustomerView());
                }

                case "Reports" -> {
                    topBar.setTitle("Sales Report");
                    contentPane.getChildren().setAll(buildReportsPanel());
                }

                case "Logout" -> {
                    if (AlertHelper.confirm("Logout", "Are you sure you want to logout?")) {
                        currentStage.close();
                        SessionManager.stopSession();
                        SessionManager.logout();
                        new LoginScreen().start(new Stage());
                    }
                }

                default -> {
                    topBar.setTitle(menu);
                    contentPane.getChildren().setAll(
                            new Label(menu + " coming soon")
                    );
                }
            }
        });

        // Wire logout button in topbar
        topBar.getLogoutButton().setOnAction(e -> {
            if (AlertHelper.confirm("Logout", "Are you sure you want to logout?")) {
                currentStage.close();
                SessionManager.stopSession();
                SessionManager.logout();
                new LoginScreen().start(new Stage());
            }
        });

        // Load dashboard on start
        contentPane.getChildren().setAll(new DashboardView());
    }

    // ===================================================
    // MATERIALS PANEL — view only for sales staff
    // ===================================================
    private VBox buildMaterialsPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));

        Label heading = new Label("📦 Available Materials");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        TableView<Material> table = new TableView<>();
        TableUtils.configureTableLayout(table);

        TableColumn<Material, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("materialId"));
        idCol.setMinWidth(80);
        
        TableColumn<Material, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("materialName"));
        nameCol.setMinWidth(150);
        
        TableColumn<Material, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        catCol.setMinWidth(120);
        
        TableColumn<Material, Integer> qtyCol = new TableColumn<>("In Stock");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setMinWidth(100);
        
        TableColumn<Material, Double> priceCol = new TableColumn<>("Unit Price (KES)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        priceCol.setMinWidth(130);
        
        TableColumn<Material, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unitOfMeasure"));
        unitCol.setMinWidth(100);

        table.getColumns().addAll(idCol, nameCol, catCol, qtyCol, priceCol, unitCol);

        ObservableList<Material> allMaterials = controller.findMaterial(null) != null
                ? javafx.collections.FXCollections.observableArrayList()
                : javafx.collections.FXCollections.observableArrayList();

        // Load via DAO through controller helper
        reloadMaterials(table);

        // Search
        TextField searchField = new TextField();
        searchField.setPromptText("Search materials...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, old, newVal) -> {
            reloadMaterials(table);
        });

        Button refreshBtn = styledButton("🔄 Refresh", "#3b82f6");
        refreshBtn.setOnAction(e -> reloadMaterials(table));

        HBox toolbar = new HBox(10, refreshBtn);
        VBox.setVgrow(table, Priority.ALWAYS);
        panel.getChildren().addAll(heading, searchField, table, toolbar);
        return panel;
    }

    private void reloadMaterials(TableView<Material> table) {
        // Use SalesController which wraps MaterialDAO
        database.MaterialDAo dao = new database.MaterialDAo();
        table.setItems(
                javafx.collections.FXCollections.observableArrayList(dao.getAllMaterials())
        );
    }

    // ===================================================
    // SALES PANEL — issue materials + view sales history
    // ===================================================
    private VBox buildSalesPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));

        Label heading = new Label("🛒 Issue Material");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        // Stats row
        HBox statsRow = new HBox(15);
        DashboardCard todaySalesCard = new DashboardCard(
                "Today's Sales",
                String.valueOf(controller.getTodaySalesCount()),
                "Transactions"
        );
        DashboardCard todayRevenueCard = new DashboardCard(
                "Today's Revenue",
                String.format("KES %,.0f", controller.getTodayRevenue()),
                "Collected Today"
        );
        DashboardCard totalSalesCard = new DashboardCard(
                "Total Sales",
                String.valueOf(controller.getTotalSalesCount()),
                "All Time"
        );
        statsRow.getChildren().addAll(todaySalesCard, todayRevenueCard, totalSalesCard);

        // Sales table
        TableView<Sale> table = new TableView<>();
        TableUtils.configureTableLayout(table);

        TableColumn<Sale, Integer> idCol = new TableColumn<>("Sale ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("salesId"));
        idCol.setMinWidth(90);
        
        TableColumn<Sale, Integer> custCol = new TableColumn<>("Customer ID");
        custCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        custCol.setMinWidth(110);
        
        TableColumn<Sale, String> matCol = new TableColumn<>("Material");
        matCol.setCellValueFactory(new PropertyValueFactory<>("materialName"));
        matCol.setMinWidth(150);
        
        TableColumn<Sale, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setMinWidth(80);
        
        TableColumn<Sale, Double> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        priceCol.setMinWidth(120);
        
        TableColumn<Sale, Double> totalCol = new TableColumn<>("Total (KES)");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        totalCol.setMinWidth(120);

        table.getColumns().addAll(idCol, custCol, matCol, qtyCol, priceCol, totalCol);
        table.setItems(controller.getAllSales());

        // Buttons
        Button issueSaleBtn = styledButton("➕ Issue Material", "#10b981");
        Button refreshBtn = styledButton("🔄 Refresh", "#3b82f6");

        issueSaleBtn.setOnAction(e -> showIssueMaterialDialog(table));
        refreshBtn.setOnAction(e -> table.setItems(controller.getAllSales()));

        HBox toolbar = new HBox(10, issueSaleBtn, refreshBtn);
        VBox.setVgrow(table, Priority.ALWAYS);
        panel.getChildren().addAll(heading, statsRow, table, toolbar);
        return panel;
    }

    private void showIssueMaterialDialog(TableView<Sale> table) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Issue Material to Customer");

        TextField customerIdField = new TextField();
        customerIdField.setPromptText("Customer ID");
        TextField materialNameField = new TextField();
        materialNameField.setPromptText("Material Name");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");

        CheckBox creditBox = new CheckBox("Credit Sale (generate invoice)");
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(30));
        datePicker.setVisible(false);
        creditBox.setOnAction(e -> datePicker.setVisible(creditBox.isSelected()));

        Label msgLabel = new Label("");
        msgLabel.setTextFill(Color.RED);

        Button issueBtn = styledButton("✅ Issue", "#10b981");
        Button cancelBtn = styledButton("Cancel", "#ef4444");

        issueBtn.setOnAction(e -> {
            try {
                int customerId = Integer.parseInt(customerIdField.getText().trim());
                String materialName = materialNameField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());

                // Look up details before issuing (for receipt)
                Customer customer = controller.findCustomer(customerId);
                Material material = controller.findMaterial(materialName);

                if (customer == null) { msgLabel.setText("Customer not found."); return; }
                if (material == null) { msgLabel.setText("Material not found."); return; }

                String err;
                if (creditBox.isSelected()) {
                    if (datePicker.getValue() == null) {
                        msgLabel.setText("Please select a due date.");
                        return;
                    }
                    err = controller.issueMaterialOnCredit(customerId, materialName, quantity, datePicker.getValue());
                } else {
                    err = controller.issueMaterial(customerId, materialName, quantity);
                }

                if (err != null) { msgLabel.setText(err); return; }

                Sale sale = controller.getLastSale();
                table.setItems(controller.getAllSales());
                dialog.close();

                // Show receipt
                if (sale != null) showReceipt(customer, material, quantity, sale, creditBox.isSelected());

            } catch (NumberFormatException ex) {
                msgLabel.setText("Please enter valid numbers.");
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        GridPane form = new GridPane();
        form.setHgap(12); form.setVgap(12); form.setPadding(new Insets(25));
        form.addRow(0, new Label("Customer ID:"), customerIdField);
        form.addRow(1, new Label("Material Name:"), materialNameField);
        form.addRow(2, new Label("Quantity:"), quantityField);
        form.add(creditBox, 0, 3, 2, 1);
        form.addRow(4, new Label("Due Date:"), datePicker);
        form.add(msgLabel, 0, 5, 2, 1);
        form.add(new HBox(10, issueBtn, cancelBtn), 0, 6, 2, 1);

        dialog.setScene(new Scene(form, 380, 320));
        dialog.showAndWait();
    }

    // ===================================================
    // REPORTS PANEL
    // ===================================================
    private VBox buildReportsPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));

        Label heading = new Label("📊 Sales Report");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setFont(Font.font("Monospaced", 13));
        VBox.setVgrow(reportArea, Priority.ALWAYS);

        Button salesBtn = styledButton("💰 Generate Sales Report", "#10b981");
        salesBtn.setOnAction(e -> reportArea.setText(controller.getSalesReport()));

        // Auto-load on open
        reportArea.setText(controller.getSalesReport());

        HBox toolbar = new HBox(10, salesBtn);
        panel.getChildren().addAll(heading, toolbar, reportArea);
        return panel;
    }

    // ===================================================
    // RECEIPT
    // ===================================================
    private void showReceipt(Customer customer, Material material,
                             int quantity, Sale sale, boolean isCredit) {
        Stage receiptStage = new Stage();
        receiptStage.initModality(Modality.APPLICATION_MODAL);
        receiptStage.setTitle("Sale Receipt");

        String dateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        Label shopName = new Label("CONSTRUCTION WAREHOUSE");
        shopName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        Label shopSub = new Label("Materials & Hardware Store");
        shopSub.setFont(Font.font("Segoe UI", 12));
        shopSub.setTextFill(Color.GRAY);

        Label div1 = mono("================================================");
        Label receiptTitle = new Label(isCredit ? "CREDIT SALE RECEIPT" : "OFFICIAL RECEIPT");
        receiptTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        if (isCredit) receiptTitle.setTextFill(Color.web("#f59e0b"));
        Label div2 = mono("================================================");

        Label dateLabel = mono("Date       : " + dateTime);
        Label saleIdLabel = mono("Sale ID    : " + sale.getSalesId());
        Label div3 = mono("------------------------------------------------");

        Label custHeading = new Label("CUSTOMER DETAILS");
        custHeading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        Label custId    = mono("Customer ID: " + customer.getCustomerId());
        Label custName  = mono("Name       : " + customer.getCustomerName());
        Label custPhone = mono("Phone      : " + customer.getPhoneNumber());
        Label div4 = mono("------------------------------------------------");

        Label itemHeading = new Label("ITEM DETAILS");
        itemHeading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        Label matLabel   = mono("Material   : " + material.getMaterialName());
        Label catLabel   = mono("Category   : " + material.getCategory());
        Label unitLabel  = mono("Unit       : " + material.getUnitOfMeasure());
        Label qtyLabel   = mono("Quantity   : " + quantity);
        Label priceLabel = mono("Unit Price : KES " + String.format("%.2f", material.getUnitPrice()));
        Label div5 = mono("------------------------------------------------");

        Label totalLabel = new Label("TOTAL      : KES " + String.format("%.2f", sale.getTotalAmount()));
        totalLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 14));
        totalLabel.setTextFill(Color.web("#10b981"));

        Label paymentLabel = new Label(isCredit ? "PAYMENT    : CREDIT (Invoice Generated)" : "PAYMENT    : CASH");
        paymentLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
        paymentLabel.setTextFill(isCredit ? Color.web("#f59e0b") : Color.web("#10b981"));

        Label div6 = mono("================================================");
        Label thanks = new Label("Thank you for your business!");
        thanks.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        thanks.setTextFill(Color.GRAY);

        Button closeBtn = styledButton("Close", "#6b7280");
        closeBtn.setOnAction(e -> receiptStage.close());
        HBox btnBox = new HBox(closeBtn); btnBox.setAlignment(Pos.CENTER);

        VBox receiptBox = new VBox(6,
                shopName, shopSub, div1, receiptTitle, div2,
                dateLabel, saleIdLabel, div3,
                custHeading, custId, custName, custPhone, div4,
                itemHeading, matLabel, catLabel, unitLabel, qtyLabel, priceLabel, div5,
                totalLabel, paymentLabel, div6, thanks, btnBox
        );
        receiptBox.setPadding(new Insets(25));
        receiptBox.setStyle("-fx-background-color: white;");

        ScrollPane scroll = new ScrollPane(receiptBox);
        scroll.setFitToWidth(true);
        receiptStage.setScene(new Scene(scroll, 480, 580));
        receiptStage.setResizable(false);
        receiptStage.show();
    }

    // ===================================================
    // HELPERS
    // ===================================================
    private Button styledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color:" + color + ";" +
                        "-fx-text-fill:white;" +
                        "-fx-padding:10 20;" +
                        "-fx-font-size:13;" +
                        "-fx-cursor:hand;" +
                        "-fx-background-radius:6;"
        );
        return btn;
    }

    private Label mono(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Monospaced", 12));
        return lbl;
    }
}