package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import util.SessionManager;
import model.User;

public class TopBar extends HBox {

    private final Label userLabel;

    public TopBar(String titleText) {
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER_LEFT);
        setStyle("-fx-background-color: #f8f9fa;");

        Label title = new Label(titleText);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Get logged-in user from session
        User currentUser = SessionManager.getCurrentUser();
        String username = (currentUser != null) ? currentUser.getUsername() : "Unknown";
        String role = (currentUser != null) ? currentUser.getRole() : "";

        userLabel = new Label(String.format("👤 %s (%s)", username, role));
        userLabel.setFont(Font.font("Segoe UI", 13));
        userLabel.setStyle("-fx-text-fill: #34495e;");

        getChildren().addAll(title, spacer, userLabel);
    }

    /**
     * Update the title displayed in the top bar.
     */
    public void setTitle(String newTitle) {
        Label titleLabel = (Label) getChildren().get(0);
        titleLabel.setText(newTitle);
    }

    /**
     * Update the user information displayed.
     */
    public void updateUserInfo() {
        User currentUser = SessionManager.getCurrentUser();
        String username = (currentUser != null) ? currentUser.getUsername() : "Unknown";
        String role = (currentUser != null) ? currentUser.getRole() : "";
        userLabel.setText(String.format("👤 %s (%s)", username, role));
    }
}