package ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DashboardCard extends VBox {

    private final Label valueLabel;
    private final Label footerLabel;

    public DashboardCard(String title, String value, String footer) {

        setSpacing(10);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER_LEFT);

        setPrefWidth(220);
        setPrefHeight(130);

        setStyle("""
                -fx-background-color:white;
                -fx-background-radius:12;
                -fx-border-radius:12;
                -fx-border-color:#E5E7EB;
                -fx-effect:dropshadow(gaussian,rgba(0,0,0,0.12),8,0,0,2);
                """);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));

        footerLabel = new Label(footer);
        footerLabel.setStyle("-fx-text-fill:#6B7280;");

        getChildren().addAll(
                titleLabel,
                valueLabel,
                footerLabel
        );
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setFooter(String footer) {
        footerLabel.setText(footer);
    }
}