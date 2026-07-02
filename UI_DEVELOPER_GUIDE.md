# UI Components Developer Guide

## Quick Reference for New UI Components

### 1. StyledButton

Reusable button with predefined styles and colors.

**Factory Methods**:
```java
import ui.StyledButton;

// Primary (blue)
StyledButton primaryBtn = StyledButton.primary("Click Me");

// Success (green)
StyledButton saveBtn = StyledButton.success("Save");

// Danger (red)
StyledButton deleteBtn = StyledButton.danger("Delete");

// Warning (orange)
StyledButton warnBtn = StyledButton.warning("Warning");

// Secondary (gray)
StyledButton secondaryBtn = StyledButton.secondary("Cancel");

// Or create custom
StyledButton customBtn = new StyledButton(
    "Custom", 
    "#FF5733",  // normal color
    "#C70039"   // hover color
);
```

**Usage in Layouts**:
```java
HBox buttonBox = new HBox(10);
buttonBox.getChildren().addAll(
    StyledButton.success("Save"),
    StyledButton.secondary("Cancel")
);
```

---

### 2. Enhanced TopBar

Shows logged-in user information automatically.

```java
import ui.TopBar;
import model.User;

// Create top bar with title
TopBar topBar = new TopBar("Dashboard");

// Change title
topBar.setTitle("New Page Title");

// Update user info if session changes
topBar.updateUserInfo();
```

**Features**:
- Automatically displays username and role
- Reads from `SessionManager.getCurrentUser()`
- Gray background for visual separation

---

### 3. Material Form Dialog

Add or edit materials.

```java
import ui.forms.MaterialForm;
import model.Material;

// Add new material
Button addBtn = StyledButton.success("Add Material");
addBtn.setOnAction(e -> {
    MaterialForm form = new MaterialForm(null, () -> {
        loadMaterials(); // Refresh table after save
    });
    form.showAndWait();
});

// Edit existing material
Material selected = materialsTable.getSelectionModel().getSelectedItem();
if (selected != null) {
    MaterialForm form = new MaterialForm(selected, this::loadMaterials);
    form.showAndWait();
}
```

**Form Fields**:
- Material ID (auto-increment, disabled in edit mode)
- Material Name
- Category
- Quantity
- Unit Price
- Unit of Measure
- Minimum Stock Level
- Supplier ID

---

### 4. Supplier Form Dialog

Add or edit suppliers.

```java
import ui.forms.SupplierForm;
import model.Supplier;

// Add new supplier
Button addBtn = StyledButton.success("Add Supplier");
addBtn.setOnAction(e -> {
    SupplierForm form = new SupplierForm(null, this::loadSuppliers);
    form.showAndWait();
});

// Edit existing
Supplier selected = suppliersTable.getSelectionModel().getSelectedItem();
if (selected != null) {
    SupplierForm form = new SupplierForm(selected, this::loadSuppliers);
    form.showAndWait();
}
```

**Form Fields**:
- Supplier ID
- Supplier Name
- Phone Number
- Email
- Location

---

### 5. Sales Form Dialog

Record sales transactions.

```java
import ui.forms.SalesForm;
import model.Sale;

// Record new sale
Button saleBtn = StyledButton.success("Record Sale");
saleBtn.setOnAction(e -> {
    SalesForm form = new SalesForm(null, this::loadSales);
    form.showAndWait();
});
```

**Form Features**:
- Auto-calculates total amount (Quantity × Unit Price)
- Validates customer, material, quantity
- Handles business logic through `SalesController.issueMaterial()`
- Displays meaningful error messages

---

### 6. UIConstants - Color & Spacing

Centralized theming constants.

```java
import ui.util.UIConstants;

// Colors
String primary = UIConstants.PRIMARY_COLOR;    // #2c3e50
String success = UIConstants.SUCCESS_COLOR;    // #27ae60
String danger = UIConstants.DANGER_COLOR;      // #e74c3c

// Spacing
int large = UIConstants.SPACING_LARGE;         // 15
int medium = UIConstants.SPACING_MEDIUM;       // 10
int small = UIConstants.SPACING_SMALL;         // 5

// Font sizes
int titleSize = UIConstants.FONT_TITLE;        // 22
int bodySize = UIConstants.FONT_BODY;          // 14

// Helper methods
String buttonStyle = UIConstants.getButtonStyle(UIConstants.SUCCESS_COLOR);
String cardStyle = UIConstants.getCardStyle();
String fieldStyle = UIConstants.getTextFieldStyle();
```

---

## Complete Example: Creating a Materials View

```java
package ui.views;

import controller.MaterialController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Material;
import ui.StyledButton;
import ui.forms.MaterialForm;
import ui.util.UIConstants;

public class MaterialView extends VBox {

    private final MaterialController controller = new MaterialController();
    private final TableView<Material> table = new TableView<>();
    private final TextField searchField = new TextField();

    public MaterialView() {
        setSpacing(UIConstants.SPACING_LARGE);
        setPadding(new Insets(UIConstants.SPACING_XL));

        // Header
        Label title = new Label("Inventory Management");
        title.setStyle("-fx-font-size: " + UIConstants.FONT_TITLE + "px;");
        
        // Toolbar
        HBox toolbar = createToolbar();
        
        // Table
        setupTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        getChildren().addAll(title, searchField, toolbar, table);
        loadMaterials();
    }

    private HBox createToolbar() {
        StyledButton addBtn = StyledButton.success("Add Material");
        StyledButton editBtn = StyledButton.primary("Edit");
        StyledButton deleteBtn = StyledButton.danger("Delete");
        StyledButton refreshBtn = StyledButton.secondary("Refresh");

        addBtn.setOnAction(e -> {
            MaterialForm form = new MaterialForm(null, this::loadMaterials);
            form.showAndWait();
        });

        editBtn.setOnAction(e -> {
            Material selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                MaterialForm form = new MaterialForm(selected, this::loadMaterials);
                form.showAndWait();
            }
        });

        deleteBtn.setOnAction(e -> {
            Material selected = table.getSelectionModel().getSelectedItem();
            if (selected != null && controller.deleteMaterial(selected.getMaterialId())) {
                loadMaterials();
            }
        });

        refreshBtn.setOnAction(e -> loadMaterials());

        HBox toolbar = new HBox(UIConstants.SPACING_MEDIUM);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getChildren().addAll(addBtn, editBtn, deleteBtn, refreshBtn);
        return toolbar;
    }

    private void setupTable() {
        TableColumn<Material, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("materialId"));
        idCol.setPrefWidth(80);

        TableColumn<Material, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("materialName"));
        nameCol.setPrefWidth(200);

        TableColumn<Material, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        catCol.setPrefWidth(150);

        TableColumn<Material, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setPrefWidth(100);

        TableColumn<Material, Double> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        priceCol.setPrefWidth(120);

        table.getColumns().addAll(idCol, nameCol, catCol, qtyCol, priceCol);
    }

    private void loadMaterials() {
        ObservableList<Material> materials = 
            FXCollections.observableArrayList(controller.getAllMaterials());
        table.setItems(materials);
    }
}
```

---

## Best Practices

1. **Always use StyledButton** for consistency
   ```java
   ✅ StyledButton.success("Save")
   ❌ new Button("Save")
   ```

2. **Use UIConstants** for spacing and colors
   ```java
   ✅ grid.setVgap(UIConstants.SPACING_LARGE);
   ❌ grid.setVgap(20);
   ```

3. **Handle form callbacks** to refresh data
   ```java
   ✅ new MaterialForm(null, this::loadMaterials);
   ❌ new MaterialForm(null, null);
   ```

4. **Check selection before editing**
   ```java
   ✅ if (selected != null) { /* edit */ }
   ❌ /* edit without checking */
   ```

5. **Use AlertHelper** for user feedback
   ```java
   ✅ AlertHelper.success("Material added");
   ❌ System.out.println("Material added");
   ```

---

## Testing the UI

After integration, verify:
- [ ] Buttons have correct colors
- [ ] Hover effects work smoothly
- [ ] Forms validate inputs properly
- [ ] Success alerts appear
- [ ] Error messages are helpful
- [ ] Forms close after save
- [ ] Data refreshes in tables
- [ ] User name displays in TopBar

---

**Happy coding! 🎨**

