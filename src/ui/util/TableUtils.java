package ui.util;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;

/**
 * Utility class for handling table column sizing and text truncation
 */
public class TableUtils {

    /**
     * Configure table to use smart column sizing with proper text wrapping
     */
    public static <T> void configureTableLayout(TableView<T> table) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("""
                -fx-font-size: 13px;
                -fx-table-cell-border-color: #f0f0f0;
                """);
    }

    /**
     * Set minimum width for a table column to prevent excessive truncation
     */
    public static <T, U> void setColumnMinWidth(TableColumn<T, U> column, double minWidth) {
        column.setMinWidth(minWidth);
        column.setPrefWidth(minWidth);
    }

    /**
     * Configure a column with wrappable text
     */
    public static <T> void setWrappableTextColumn(TableColumn<T, String> column) {
        column.setCellFactory(col -> {
            TextFieldTableCell<T, String> cell = new TextFieldTableCell<>(new DefaultStringConverter());
            cell.setStyle("""
                    -fx-text-overrun: center-word-ellipsis;
                    -fx-text-alignment: left;
                    -fx-padding: 5px;
                    """);
            return cell;
        });
    }

    /**
     * Auto-fit all columns based on content
     */
    public static <T> void autoFitColumns(TableView<T> table) {
        table.getColumns().forEach(column -> {
            column.setPrefWidth(column.getPrefWidth() * 1.2);
        });
    }
}

