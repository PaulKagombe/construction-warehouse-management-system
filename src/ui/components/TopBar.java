package ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TopBar extends BorderPane {

    private final Label titleLabel;
    private final Label userLabel;
    private final Label dateLabel;
    private final Button logoutButton;

    public TopBar(User user) {

        setPadding(new Insets(15));
        setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #dcdcdc;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        // Left Side
        titleLabel = new Label("Warehouse Management System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        setLeft(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.CENTER_LEFT);

        // Right Side
        userLabel = new Label(
                "👤 " + user.getUsername() +
                        " (" + user.getRole() + ")"
        );

        userLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        dateLabel = new Label(
                LocalDate.now().format(
                        DateTimeFormatter.ofPattern("dd MMM yyyy")
                )
        );

        logoutButton = new Button("Logout");

        logoutButton.setStyle(
                "-fx-background-color:#E74C3C;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-weight:bold;" +
                        "-fx-cursor:hand;"
        );

        HBox rightBox = new HBox(15);

        rightBox.setAlignment(Pos.CENTER_RIGHT);

        rightBox.getChildren().addAll(
                dateLabel,
                userLabel,
                logoutButton
        );

        setRight(rightBox);
    }

    public Button getLogoutButton() {
        return logoutButton;
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}