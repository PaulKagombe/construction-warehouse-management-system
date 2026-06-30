package ui;

import controller.AdminController;
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
import model.*;
import ui.util.TableUtils;
import ui.views.CustomerView;
import ui.views.DashboardView;
import util.AlertHelper;
import util.SessionManager;

import java.time.LocalDate;

public class AdminDashboard extends BaseDashboard {

    private final AdminController controller;

    public AdminDashboard(User user) {
        super(user);
        this.controller = new AdminController();
    }

    @Override
    protected void initialize() {
        // Add Admin-only menu items to the sidebar listener
        sidebar.setMenuSelectionListener(menu -> {

            switch (menu) {

                case "Dashboard" -> {
                    topBar.setTitle("Dashboard");
                    contentPane.getChildren().setAll(new DashboardView());
                }

                case "Inventory" -> {
                    topBar.setTitle("Inventory");
                    contentPane.getChildren().setAll(buildMaterialsPanel());
                }

                case "Customers" -> {
                    topBar.setTitle("Customers");
                    contentPane.getChildren().setAll(new CustomerView());
                }

                case "Suppliers" -> {
                    topBar.setTitle("Suppliers");
                    contentPane.getChildren().setAll(buildSuppliersPanel());
                }

                case "Sales" -> {
                    topBar.setTitle("Sales");
                    contentPane.getChildren().setAll(buildSalesPanel());
                }

                case "Reports" -> {
                    topBar.setTitle("Reports");
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

        // Session timeout
        topBar.getLogoutButton().setOnAction(e -> {
            if (AlertHelper.confirm("Logout", "Are you sure you want to logout?")) {
                currentStage.close();
                SessionManager.stopSession();
                SessionManager.logout();
                new LoginScreen().start(new Stage());
            }
        });
    }

    // ===================================================
    // MATERIALS PANEL
    // ===================================================
    private VBox buildMaterialsPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));

        Label heading = new Label("📦 Materials Management");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        TableView<Material> table = new TableView<>();
        TableUtils.configureTableLayout(table);

        TableColumn<Material, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("materialId"));
        idCol.setMinWidth(70);
        
        TableColumn<Material, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("materialName"));
        nameCol.setMinWidth(140);
        
        TableColumn<Material, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        catCol.setMinWidth(120);
        
        TableColumn<Material, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setMinWidth(80);
        
        TableColumn<Material, Double> priceCol = new TableColumn<>("Unit Price (KES)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        priceCol.setMinWidth(120);
        
        TableColumn<Material, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unitOfMeasure"));
        unitCol.setMinWidth(90);
        
        TableColumn<Material, Integer> minCol = new TableColumn<>("Min Stock");
        minCol.setCellValueFactory(new PropertyValueFactory<>("minimumStockLevel"));
        minCol.setMinWidth(100);

        table.getColumns().addAll(idCol, nameCol, catCol, qtyCol, priceCol, unitCol, minCol);
        
        // Search
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name or category...");
        searchField.setPrefWidth(300);
        ObservableList<Material> all = controller.getMaterials();
        table.setItems(all);
        searchField.textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.isBlank()) { table.setItems(all); return; }
            String lower = newVal.toLowerCase();
            table.setItems(all.filtered(m ->
                    m.getMaterialName().toLowerCase().contains(lower) ||
                            m.getCategory().toLowerCase().contains(lower)));
        });

        // Buttons
        Button addBtn    = styledButton("➕ Add", "#10b981");
        Button editBtn   = styledButton("✏️ Edit", "#f59e0b");
        Button deleteBtn = styledButton("🗑️ Delete", "#ef4444");
        Button refreshBtn = styledButton("🔄 Refresh", "#3b82f6");

        addBtn.setOnAction(e -> showAddMaterialDialog(table));
        editBtn.setOnAction(e -> {
            Material s = table.getSelectionModel().getSelectedItem();
            if (s == null) { alert("Please select a material to edit."); return; }
            showEditMaterialDialog(table, s);
        });
        deleteBtn.setOnAction(e -> {
            Material s = table.getSelectionModel().getSelectedItem();
            if (s == null) { alert("Please select a material to delete."); return; }
            if (AlertHelper.confirm("Delete", "Delete " + s.getMaterialName() + "?")) {
                String err = controller.deleteMaterial(s.getMaterialId());
                if (err != null) alert(err);
                else table.setItems(controller.getMaterials());
            }
        });
        refreshBtn.setOnAction(e -> table.setItems(controller.getMaterials()));

        HBox toolbar = new HBox(10, addBtn, editBtn, deleteBtn, refreshBtn);
        VBox.setVgrow(table, Priority.ALWAYS);
        panel.getChildren().addAll(heading, searchField, table, toolbar);
        return panel;
    }

    private void showAddMaterialDialog(TableView<Material> table) {
        Stage d = new Stage(); d.initModality(Modality.APPLICATION_MODAL); d.setTitle("Add Material");
        TextField idF = new TextField(); idF.setPromptText("ID");
        TextField nameF = new TextField(); nameF.setPromptText("Name");
        TextField catF = new TextField(); catF.setPromptText("Category");
        TextField qtyF = new TextField(); qtyF.setPromptText("Quantity");
        TextField priceF = new TextField(); priceF.setPromptText("Unit Price");
        TextField unitF = new TextField(); unitF.setPromptText("Unit");
        TextField minF = new TextField(); minF.setPromptText("Min Stock");
        TextField supF = new TextField(); supF.setPromptText("Supplier ID");
        Label msg = new Label(""); msg.setTextFill(Color.RED);

        Button save = styledButton("Save", "#10b981");
        Button cancel = styledButton("Cancel", "#ef4444");
        save.setOnAction(e -> {
            try {
                String err = controller.addMaterial(
                        Integer.parseInt(idF.getText().trim()), nameF.getText().trim(),
                        catF.getText().trim(), Integer.parseInt(qtyF.getText().trim()),
                        Double.parseDouble(priceF.getText().trim()), unitF.getText().trim(),
                        Integer.parseInt(minF.getText().trim()), Integer.parseInt(supF.getText().trim()));
                if (err != null) { msg.setText(err); return; }
                table.setItems(controller.getMaterials()); d.close();
            } catch (NumberFormatException ex) { msg.setText("Invalid numbers entered."); }
        });
        cancel.setOnAction(e -> d.close());

        GridPane form = new GridPane(); form.setHgap(10); form.setVgap(10); form.setPadding(new Insets(20));
        form.addRow(0, new Label("ID:"), idF); form.addRow(1, new Label("Name:"), nameF);
        form.addRow(2, new Label("Category:"), catF); form.addRow(3, new Label("Quantity:"), qtyF);
        form.addRow(4, new Label("Unit Price:"), priceF); form.addRow(5, new Label("Unit:"), unitF);
        form.addRow(6, new Label("Min Stock:"), minF); form.addRow(7, new Label("Supplier ID:"), supF);
        form.add(msg, 0, 8, 2, 1); form.add(new HBox(10, save, cancel), 0, 9, 2, 1);
        d.setScene(new Scene(form, 400, 430)); d.showAndWait();
    }

    private void showEditMaterialDialog(TableView<Material> table, Material m) {
        Stage d = new Stage(); d.initModality(Modality.APPLICATION_MODAL); d.setTitle("Edit Material");
        TextField nameF = new TextField(m.getMaterialName());
        TextField catF = new TextField(m.getCategory());
        TextField qtyF = new TextField(String.valueOf(m.getQuantity()));
        TextField priceF = new TextField(String.valueOf(m.getUnitPrice()));
        TextField unitF = new TextField(m.getUnitOfMeasure());
        TextField minF = new TextField(String.valueOf(m.getMinimumStockLevel()));
        TextField supF = new TextField(String.valueOf(m.getSupplierId()));
        Label msg = new Label(""); msg.setTextFill(Color.RED);

        Button save = styledButton("Save", "#10b981");
        Button cancel = styledButton("Cancel", "#ef4444");
        save.setOnAction(e -> {
            try {
                String err = controller.updateMaterial(m, nameF.getText().trim(), catF.getText().trim(),
                        Integer.parseInt(qtyF.getText().trim()), Double.parseDouble(priceF.getText().trim()),
                        unitF.getText().trim(), Integer.parseInt(minF.getText().trim()), Integer.parseInt(supF.getText().trim()));
                if (err != null) { msg.setText(err); return; }
                table.setItems(controller.getMaterials()); d.close();
            } catch (NumberFormatException ex) { msg.setText("Invalid numbers entered."); }
        });
        cancel.setOnAction(e -> d.close());

        GridPane form = new GridPane(); form.setHgap(10); form.setVgap(10); form.setPadding(new Insets(20));
        form.addRow(0, new Label("ID:"), new Label(String.valueOf(m.getMaterialId())));
        form.addRow(1, new Label("Name:"), nameF); form.addRow(2, new Label("Category:"), catF);
        form.addRow(3, new Label("Quantity:"), qtyF); form.addRow(4, new Label("Unit Price:"), priceF);
        form.addRow(5, new Label("Unit:"), unitF); form.addRow(6, new Label("Min Stock:"), minF);
        form.addRow(7, new Label("Supplier ID:"), supF);
        form.add(msg, 0, 8, 2, 1); form.add(new HBox(10, save, cancel), 0, 9, 2, 1);
        d.setScene(new Scene(form, 400, 430)); d.showAndWait();
    }

    // ===================================================
    // SUPPLIERS PANEL
    // ===================================================
    private VBox buildSuppliersPanel() {
        VBox panel = new VBox(15); panel.setPadding(new Insets(20));
        Label heading = new Label("🚚 Suppliers"); heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        TableView<Supplier> table = new TableView<>();
        TableUtils.configureTableLayout(table);
        
        TableColumn<Supplier, Integer> idCol = new TableColumn<>("ID"); 
        idCol.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        idCol.setMinWidth(80);
        
        TableColumn<Supplier, String> nameCol = new TableColumn<>("Name"); 
        nameCol.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        nameCol.setMinWidth(150);
        
        TableColumn<Supplier, String> phoneCol = new TableColumn<>("Phone"); 
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        phoneCol.setMinWidth(130);
        
        TableColumn<Supplier, String> emailCol = new TableColumn<>("Email"); 
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setMinWidth(180);
        
        TableColumn<Supplier, String> locCol = new TableColumn<>("Location"); 
        locCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        locCol.setMinWidth(150);
        
        table.getColumns().addAll(idCol, nameCol, phoneCol, emailCol, locCol);
        table.setItems(controller.getSuppliers());

        Button refreshBtn = styledButton("🔄 Refresh", "#3b82f6");
        refreshBtn.setOnAction(e -> table.setItems(controller.getSuppliers()));

        HBox toolbar = new HBox(10, refreshBtn);
        VBox.setVgrow(table, Priority.ALWAYS);
        panel.getChildren().addAll(heading, table, toolbar);
        return panel;
    }

    // ===================================================
    // SALES PANEL
    // ===================================================
    private VBox buildSalesPanel() {
        VBox panel = new VBox(15); panel.setPadding(new Insets(20));
        Label heading = new Label("🛒 Sales"); heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        TableView<Sale> table = new TableView<>();
        TableUtils.configureTableLayout(table);
        
        TableColumn<Sale, Integer> idCol = new TableColumn<>("ID"); 
        idCol.setCellValueFactory(new PropertyValueFactory<>("salesId"));
        idCol.setMinWidth(80);
        
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
        table.setItems(controller.getSales());

        Button issueSaleBtn = styledButton("➕ Issue Material", "#10b981");
        Button refreshBtn = styledButton("🔄 Refresh", "#3b82f6");
        issueSaleBtn.setOnAction(e -> showIssueMaterialDialog(table));
        refreshBtn.setOnAction(e -> table.setItems(controller.getSales()));

        HBox toolbar = new HBox(10, issueSaleBtn, refreshBtn);
        VBox.setVgrow(table, Priority.ALWAYS);
        panel.getChildren().addAll(heading, table, toolbar);
        return panel;
    }

    private void showIssueMaterialDialog(TableView<Sale> table) {
        Stage d = new Stage(); d.initModality(Modality.APPLICATION_MODAL); d.setTitle("Issue Material");
        TextField custIdF = new TextField(); custIdF.setPromptText("Customer ID");
        TextField matNameF = new TextField(); matNameF.setPromptText("Material Name");
        TextField qtyF = new TextField(); qtyF.setPromptText("Quantity");
        CheckBox creditBox = new CheckBox("Credit Sale (generate invoice)");
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(30));
        datePicker.setVisible(false);
        creditBox.setOnAction(e -> datePicker.setVisible(creditBox.isSelected()));
        Label msg = new Label(""); msg.setTextFill(Color.RED);

        Button issueBtn = styledButton("Issue", "#10b981");
        Button cancelBtn = styledButton("Cancel", "#ef4444");
        issueBtn.setOnAction(e -> {
            try {
                int custId = Integer.parseInt(custIdF.getText().trim());
                String matName = matNameF.getText().trim();
                int qty = Integer.parseInt(qtyF.getText().trim());
                String err;
                if (creditBox.isSelected()) {
                    err = controller.issueMaterialOnCredit(custId, matName, qty, datePicker.getValue());
                } else {
                    err = controller.issueMaterial(custId, matName, qty);
                }
                if (err != null) { msg.setText(err); return; }
                table.setItems(controller.getSales()); d.close();
            } catch (NumberFormatException ex) { msg.setText("Invalid numbers."); }
        });
        cancelBtn.setOnAction(e -> d.close());

        GridPane form = new GridPane(); form.setHgap(10); form.setVgap(10); form.setPadding(new Insets(20));
        form.addRow(0, new Label("Customer ID:"), custIdF);
        form.addRow(1, new Label("Material:"), matNameF);
        form.addRow(2, new Label("Quantity:"), qtyF);
        form.add(creditBox, 0, 3, 2, 1);
        form.addRow(4, new Label("Due Date:"), datePicker);
        form.add(msg, 0, 5, 2, 1); form.add(new HBox(10, issueBtn, cancelBtn), 0, 6, 2, 1);
        d.setScene(new Scene(form, 370, 310)); d.showAndWait();
    }

    // ===================================================
    // REPORTS PANEL
    // ===================================================
    private VBox buildReportsPanel() {
        VBox panel = new VBox(15); panel.setPadding(new Insets(20));
        Label heading = new Label("📊 Reports"); heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        TextArea reportArea = new TextArea(); reportArea.setEditable(false);
        reportArea.setFont(Font.font("Monospaced", 13)); VBox.setVgrow(reportArea, Priority.ALWAYS);

        Button stockBtn   = styledButton("📦 Stock Report", "#3b82f6");
        Button lowBtn     = styledButton("⚠️ Low Stock", "#f59e0b");
        Button salesBtn   = styledButton("💰 Sales Report", "#10b981");
        stockBtn.setOnAction(e -> reportArea.setText(controller.getStockReport()));
        lowBtn.setOnAction(e -> reportArea.setText(controller.getLowStockReport()));
        salesBtn.setOnAction(e -> reportArea.setText(controller.getSalesReport()));

        HBox buttons = new HBox(10, stockBtn, lowBtn, salesBtn);
        panel.getChildren().addAll(heading, buttons, reportArea);
        return panel;
    }

    // ===================================================
    // HELPERS
    // ===================================================
    private Button styledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color:" + color + ";-fx-text-fill:white;-fx-padding:10 20;-fx-font-size:13;-fx-cursor:hand;-fx-background-radius:6;");
        return btn;
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }
}