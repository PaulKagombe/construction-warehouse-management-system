package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Sidebar extends VBox {

    public Sidebar() {

        setPrefWidth(240);
        setSpacing(10);
        setPadding(new Insets(20));

        setStyle("""
                -fx-background-color:#1E293B;
                """);

        Label title = new Label("Warehouse System");

        title.setFont(Font.font(
                "Segoe UI",
                FontWeight.BOLD,
                22
        ));

        title.setStyle("-fx-text-fill:white;");

        getChildren().add(title);

        getChildren().add(createButton("🏠 Dashboard"));
        getChildren().add(createButton("🛒 Sales"));
        getChildren().add(createButton("👥 Customers"));
        getChildren().add(createButton("📦 Inventory"));
        getChildren().add(createButton("🚚 Suppliers"));
        getChildren().add(createButton("💳 Supplier Credit"));
        getChildren().add(createButton("📊 Reports"));

        Region spacer = new Region();

        VBox.setVgrow(spacer, Priority.ALWAYS);

        getChildren().add(spacer);

        getChildren().add(createButton("🚪 Logout"));
    }

    private Button createButton(String text) {

        Button button = new Button(text);

        button.setAlignment(Pos.CENTER_LEFT);

        button.setPrefWidth(Double.MAX_VALUE);

        button.setStyle("""
                -fx-background-color:transparent;
                -fx-text-fill:white;
                -fx-font-size:15;
                -fx-padding:12 15;
                -fx-background-radius:8;
                """);

        button.setOnMouseEntered(e ->
                button.setStyle("""
                        -fx-background-color:#334155;
                        -fx-text-fill:white;
                        -fx-font-size:15;
                        -fx-padding:12 15;
                        -fx-background-radius:8;
                        """)
        );

        button.setOnMouseExited(e ->
                button.setStyle("""
                        -fx-background-color:transparent;
                        -fx-text-fill:white;
                        -fx-font-size:15;
                        -fx-padding:12 15;
                        -fx-background-radius:8;
                        """)
        );

        return button;
    }
}