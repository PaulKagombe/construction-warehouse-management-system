package ui;

import controller.MaterialController;
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
import ui.views.DashboardView;
import util.AlertHelper;
import util.SessionManager;

public class StorekeeperDashboard extends BaseDashboard {

    private final MaterialController materialController = new MaterialController();

    public StorekeeperDashboard(User user) {
        super(user);
    }

    @Override
    protected void initialize() {

        topBar.setTitle("Storekeeper Dashboard");
        sidebar.setActive("Dashboard");

        sidebar.setMenuSelectionListener(menu -> {
            switch (menu) {

                case "Dashboard" -> {
                    topBar.setTitle("Dashboard");
                    contentPane.getChildren().setAll(new DashboardView());
                }

                case "Inventory" -> {
                    topBar.setTitle("Inventory / Stock");
                    contentPane.getChildren().setAll(buildInventoryPanel());
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

        topBar.getLogoutButton().setOnAction(e -> {
            if (AlertHelper.confirm("Logout", "Are you sure you want to logout?")) {
                currentStage.close();
                SessionManager.stopSession();
                SessionManager.logout();
                new LoginScreen().start(new Stage());
            }
        });

        contentPane.getChildren().setAll(new DashboardView());
    }

    // ===================================================
    // INVENTORY PANEL
    // ===================================================
    private VBox buildInventoryPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));

        Label heading = new Label("📦 Inventory / Stock");
        heading.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        // Stats
        HBox statsRow = new HBox(15);
        DashboardCard totalCard = new DashboardCard(
                "Total Materials",
                String.valueOf(materialController.getTotalMaterials()),
                "In System"
        );
        DashboardCard lowStockCard = new DashboardCard(
                "Low Stock Items",
                String.valueOf(materialController.getLowStockCount()),
                "Need Restocking"
        );
        DashboardCard valueCard = new DashboardCard(
                "Inventory Value",
                String.format("KES %,.0f", materialController.getTotalInventoryValue()),
                "Total Worth"
        );
        statsRow.getChildren().addAll(totalCard, lowStockCard, valueCard);

        // Table
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
        
        TableColumn<Material, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setMinWidth(100);
        
        TableColumn<Material, Integer> minCol = new TableColumn<>("Min Stock");
        minCol.setCellValueFactory(new PropertyValueFactory<>("minimumStockLevel"));
        minCol.setMinWidth(100);
        
        TableColumn<Material, Double> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        priceCol.setMinWidth(120);
        
        TableColumn<Material, String> unitCol = new TableColumn<>("Unit");
        unitCol.setCellValueFactory(new PropertyValueFactory<>("unitOfMeasure"));
        unitCol.setMinWidth(100);

        table.getColumns().addAll(idCol, nameCol, catCol, qtyCol, minCol, priceCol, unitCol);
        table.setItems(materialController.getAllMaterials());

        // Search
        TextField searchField = new TextField();
        searchField.setPromptText("Search materials...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.isBlank()) {
                table.setItems(materialController.getAllMaterials());
            } else {
                String lower = newVal.toLowerCase();
                table.setItems(materialController.getAllMaterials().filtered(m ->
                        m.getMaterialName().toLowerCase().contains(lower) ||
                                m.getCategory().toLowerCase().contains(lower)));
            }
        });

        Button receiveBtn = styledButton("📥 Receive Stock", "#10b981");
        Button updateBtn = styledButton("✏️ Update Stock", "#f59e0b");
        Button refreshBtn = styledButton("🔄 Refresh", "#3b82f6");

        receiveBtn.setOnAction(e -> showReceiveStockDialog(table));
        updateBtn.setOnAction(e -> {
            Material selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "Please select a material to update.").showAndWait();
                return;
            }
            showUpdateStockDialog(table, selected);
        });
        refreshBtn.setOnAction(e -> table.setItems(materialController.getAllMaterials()));

        HBox toolbar = new HBox(10, receiveBtn, updateBtn, refreshBtn);
        VBox.setVgrow(table, Priority.ALWAYS);
        panel.getChildren().addAll(heading, statsRow, searchField, table, toolbar);
        return panel;
    }



    // ===================================================
    // RECEIVE STOCK DIALOG
    // ===================================================
    private void showReceiveStockDialog(TableView<Material> table) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Receive Stock");

        TextField materialNameField = new TextField();
        materialNameField.setPromptText("Material Name");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity to Receive");
        Label msgLabel = new Label("");
        msgLabel.setTextFill(Color.RED);

        Button receiveBtn = styledButton("✅ Receive", "#10b981");
        Button cancelBtn = styledButton("Cancel", "#ef4444");

        receiveBtn.setOnAction(e -> {
            try {
                String materialName = materialNameField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());

                if (materialName.isEmpty() || quantity <= 0) {
                    msgLabel.setText("Please enter valid material name and quantity.");
                    msgLabel.setTextFill(Color.RED);
                    return;
                }

                Material material = materialController.findByName(materialName);
                if (material == null) {
                    msgLabel.setText("Material not found.");
                    msgLabel.setTextFill(Color.RED);
                    return;
                }

                int newQty = material.getQuantity() + quantity;
                boolean success = materialController.updateQuantity(material.getMaterialId(), newQty);
                
                if (!success) {
                    msgLabel.setText("Failed to receive stock.");
                    msgLabel.setTextFill(Color.RED);
                    return;
                }

                msgLabel.setText("Stock received successfully!");
                msgLabel.setTextFill(Color.web("#10b981"));
                table.setItems(materialController.getAllMaterials());
                
                // Close dialog after 1 second
                new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), ev -> dialog.close())
                ).play();

            } catch (NumberFormatException ex) {
                msgLabel.setText("Please enter a valid quantity number.");
                msgLabel.setTextFill(Color.RED);
            }
        });
        cancelBtn.setOnAction(e -> dialog.close());

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(25));
        form.addRow(0, new Label("Material Name:"), materialNameField);
        form.addRow(1, new Label("Quantity:"), quantityField);
        form.add(msgLabel, 0, 2, 2, 1);
        form.add(new HBox(10, receiveBtn, cancelBtn), 0, 3, 2, 1);

        dialog.setScene(new Scene(form, 380, 250));
        dialog.showAndWait();
    }

    // ===================================================
    // UPDATE STOCK DIALOG
    // ===================================================
    private void showUpdateStockDialog(TableView<Material> table, Material material) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Update Stock");

        Label materialLabel = new Label(material.getMaterialName());
        Label currentQtyLabel = new Label("Current: " + material.getQuantity());
        currentQtyLabel.setStyle("-fx-text-fill: #6B7280;");
        
        TextField newQuantityField = new TextField(String.valueOf(material.getQuantity()));
        Label msgLabel = new Label("");
        msgLabel.setTextFill(Color.RED);

        Button updateBtn = styledButton("✅ Update", "#10b981");
        Button cancelBtn = styledButton("Cancel", "#ef4444");

        updateBtn.setOnAction(e -> {
            try {
                int newQuantity = Integer.parseInt(newQuantityField.getText().trim());

                if (newQuantity < 0) {
                    msgLabel.setText("Quantity cannot be negative.");
                    msgLabel.setTextFill(Color.RED);
                    return;
                }

                boolean success = materialController.updateQuantity(material.getMaterialId(), newQuantity);
                if (!success) {
                    msgLabel.setText("Failed to update stock.");
                    msgLabel.setTextFill(Color.RED);
                    return;
                }

                msgLabel.setText("Stock updated successfully!");
                msgLabel.setTextFill(Color.web("#10b981"));
                table.setItems(materialController.getAllMaterials());
                
                // Close dialog after 1 second
                new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), ev -> dialog.close())
                ).play();

            } catch (NumberFormatException ex) {
                msgLabel.setText("Please enter a valid quantity number.");
                msgLabel.setTextFill(Color.RED);
            }
        });
        cancelBtn.setOnAction(e -> dialog.close());

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(25));
        form.addRow(0, new Label("Material:"), materialLabel);
        form.addRow(1, new Label("Current Qty:"), currentQtyLabel);
        form.addRow(2, new Label("New Quantity:"), newQuantityField);
        form.add(msgLabel, 0, 3, 2, 1);
        form.add(new HBox(10, updateBtn, cancelBtn), 0, 4, 2, 1);

        dialog.setScene(new Scene(form, 380, 280));
        dialog.showAndWait();
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
}