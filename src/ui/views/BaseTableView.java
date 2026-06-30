package ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public abstract class BaseTableView<T> extends VBox {

    protected Label titleLabel;

    protected TextField searchField;

    protected Button addButton;
    protected Button editButton;
    protected Button deleteButton;
    protected Button refreshButton;

    protected TableView<T> table;

    protected HBox toolbar;

    public BaseTableView(String title) {

        setSpacing(15);
        setPadding(new Insets(20));

        titleLabel = new Label(title);

        titleLabel.setStyle("""
                -fx-font-size:22;
                -fx-font-weight:bold;
                """);

        searchField = new TextField();
        searchField.setPromptText("Search...");

        addButton = new Button("Add");
        editButton = new Button("Edit");
        deleteButton = new Button("Delete");
        refreshButton = new Button("Refresh");

        toolbar = new HBox(
                10,
                addButton,
                editButton,
                deleteButton,
                refreshButton
        );

        toolbar.setAlignment(Pos.CENTER_LEFT);

        table = new TableView<>();

        VBox.setVgrow(table, Priority.ALWAYS);

        getChildren().addAll(

                titleLabel,

                searchField,

                toolbar,

                table

        );
    }

    protected abstract void loadData();

}