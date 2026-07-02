# Text Truncation Fix Guide - Construction Warehouse System

## Problem Overview
Text truncation occurs across dashboards when:
- Table columns are too narrow to display full content
- Labels don't have enough space
- Long material names, customer names, or supplier details get cut off
- Column headers exceed available width

## Solutions Implemented

### 1. **TableUtils Utility Class** ✅
Created `ui/util/TableUtils.java` to standardize table configuration:

```java
// Configure any table with proper layout
TableUtils.configureTableLayout(table);

// Set minimum width for columns to prevent truncation
column.setMinWidth(150);
```

**Benefits:**
- Consistent table styling across all dashboards
- Automatic row height adjustment
- Proper text alignment
- Ellipsis on overflow

### 2. **Column Minimum Width Strategy** ✅
Set appropriate `setMinWidth()` for each column based on content type:

| Column Type | Recommended Min Width | Example |
|------------|----------------------|---------|
| ID | 80px | Material ID |
| Names | 140-250px | Customer Name, Material Name |
| Phone/Email | 130-180px | Phone Number, Email |
| Categories | 120px | Material Category |
| Prices | 120-130px | Unit Price |
| Quantities | 80-100px | Qty, Stock Level |

### 3. **Dashboards Updated** ✅

#### StorekeeperDashboard
- Material table: ID (80), Name (150), Category (120), Quantity (100), Min Stock (100), Price (120), Unit (100)
- Total visible width: ~770px (fits most screens)

#### AdminDashboard
- Materials table: Enhanced column sizing
- Suppliers table: ID (80), Name (150), Phone (130), Email (180), Location (150)
- Sales table: Proper spacing for all columns

#### SalesDashboardV2
- Materials table: Better visibility for sales staff
- Sales table: Clear layout for transaction records

#### CustomerView
- Customer table: ID (100), Name (250), Phone (180)
- Responsive design for customer management

### 4. **CONSTRAINED_RESIZE_POLICY** ✅
All tables now use:
```java
TableUtils.configureTableLayout(table);
```

This automatically:
- Distributes extra space evenly
- Prevents columns from shrinking below minimum width
- Maintains proportional resizing

### 5. **Text Styling for Overflow** ✅
Configured in TableUtils:
```java
-fx-text-overrun: center-word-ellipsis;  // Shows ... in middle if truncated
-fx-padding: 5px;                        // Space around text
```

## How to Add to Existing Tables

### Step 1: Import TableUtils
```java
import ui.util.TableUtils;
```

### Step 2: Configure the Table
```java
TableView<Material> table = new TableView<>();
TableUtils.configureTableLayout(table);
```

### Step 3: Set Column Widths
```java
TableColumn<Material, String> nameCol = new TableColumn<>("Name");
nameCol.setCellValueFactory(new PropertyValueFactory<>("materialName"));
nameCol.setMinWidth(150);  // Prevent truncation
```

### Step 4: Add Columns
```java
table.getColumns().addAll(idCol, nameCol, categoryCol, qtyCol);
```

## Additional Tips to Prevent Truncation

### 1. **Long Text in Dialogs**
```java
// For labels with long text
Label label = new Label("Very long text here");
label.setWrapText(true);  // Enable text wrapping
label.setMaxWidth(300);   // Set max width
```

### 2. **Dashboard Cards**
```java
// Ensure card values don't overflow
Label valueLabel = new Label(longValue);
valueLabel.setStyle("-fx-text-overrun: center-ellipsis;");
valueLabel.setMaxWidth(200);
```

### 3. **Custom Cell Factory for Special Cases**
```java
TableColumn<Material, String> nameCol = new TableColumn<>("Name");
nameCol.setCellFactory(column -> new TableCell<Material, String>() {
    private Text text;
    
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            text = new Text(item);
            text.setWrappingWidth(200);  // Wrap at 200px
            setGraphic(text);
        }
    }
});
```

## Testing Truncation

1. **Run the application**
2. **Test each dashboard:**
   - Admin Dashboard → All tables
   - Storekeeper Dashboard → Inventory
   - Sales Dashboard → Materials & Sales
   - Customer View → Customer table

3. **Resize window** and verify:
   - Text doesn't exceed column width
   - Ellipsis appears for very long text
   - Minimum widths are maintained

4. **Look for truncation in:**
   - Material names with special characters
   - Long customer names
   - Email addresses
   - Category names with multiple words

## Screen Size Considerations

### Minimum Recommended Screen Size
- **Width:** 1400px (primary view)
- **Tables typically need:** 700-900px depending on columns

### For Smaller Screens
Increase scroll bar width or use horizontal scrolling:
```java
table.setStyle("-fx-control-inner-background: white; -fx-padding: 10;");
```

## Files Modified
✅ `ui/util/TableUtils.java` (NEW)
✅ `ui/StorekeeperDashboard.java`
✅ `ui/AdminDashboard.java`
✅ `ui/SalesDashboardV2.java`
✅ `ui/views/CustomerView.java`

## Future Improvements
1. **Dynamic column sizing** based on content
2. **Horizontal scroll** for very long material names
3. **Tooltip** on hover showing full text
4. **Export to PDF** with proper formatting
5. **Responsive design** for mobile/tablet views

