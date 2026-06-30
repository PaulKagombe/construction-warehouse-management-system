package ui;

import database.UserDAO;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.User;

public class LoginScreen extends Application {

    private UserDAO userDAO = new UserDAO();
    private int loginAttempts = 0;

    @Override
    public void start(Stage stage) {

        // ===== TITLE =====
        Label titleLabel = new Label("WAREHOUSE MANAGEMENT SYSTEM");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.WHITE);

        Label subtitleLabel = new Label("Construction Materials");
        subtitleLabel.setFont(Font.font("Arial", 13));
        subtitleLabel.setTextFill(Color.LIGHTGRAY);

        VBox titleBox = new VBox(5, titleLabel, subtitleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(30, 20, 20, 20));
        titleBox.setStyle("-fx-background-color: #2c3e50;");

        // ===== FORM =====
        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefHeight(38);
        usernameField.setStyle("-fx-font-size: 13px;");

        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefHeight(38);
        passwordField.setStyle("-fx-font-size: 13px;");

        Label messageLabel = new Label("");
        messageLabel.setFont(Font.font("Arial", 12));
        messageLabel.setTextFill(Color.RED);

        Button loginButton = new Button("LOG IN");
        loginButton.setPrefWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(40);
        loginButton.setStyle(
                "-fx-background-color: #2c3e50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        // ===== LOGIN ACTION — runs on background thread to keep UI responsive =====
        loginButton.setOnAction(e -> {

            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Please enter username and password.");
                return;
            }

            // Disable UI while logging in
            loginButton.setDisable(true);
            loginButton.setText("Logging in...");
            usernameField.setDisable(true);
            passwordField.setDisable(true);
            messageLabel.setTextFill(Color.GRAY);
            messageLabel.setText("Connecting...");

            // Run database call in background thread
            Thread loginThread = new Thread(() -> {
                User user = userDAO.login(username, password);

                // Update UI back on JavaFX thread
                Platform.runLater(() -> {

                    // Re-enable UI
                    loginButton.setDisable(false);
                    loginButton.setText("LOG IN");
                    usernameField.setDisable(false);
                    passwordField.setDisable(false);

                    if (user != null) {
                        messageLabel.setTextFill(Color.GREEN);
                        messageLabel.setText("Login successful! Welcome, " + user.getUsername());
                        stage.close();
                        openDashboard(user, stage);

                    } else {
                        loginAttempts++;
                        int remaining = 3 - loginAttempts;

                        if (remaining > 0) {
                            messageLabel.setTextFill(Color.RED);
                            messageLabel.setText("Invalid credentials. " + remaining + " attempt(s) remaining.");
                            passwordField.clear();
                        } else {
                            messageLabel.setTextFill(Color.RED);
                            messageLabel.setText("Too many failed attempts. System locked.");
                            loginButton.setDisable(true);
                            usernameField.setDisable(true);
                            passwordField.setDisable(true);
                        }
                    }
                });
            });

            loginThread.setDaemon(true);
            loginThread.start();
        });

        // Allow pressing Enter to login
        passwordField.setOnAction(e -> loginButton.fire());

        VBox formBox = new VBox(12,
                usernameLabel, usernameField,
                passwordLabel, passwordField,
                messageLabel,
                loginButton
        );
        formBox.setPadding(new Insets(30, 40, 30, 40));
        formBox.setAlignment(Pos.CENTER_LEFT);
        formBox.setStyle("-fx-background-color: #f4f4f4;");

        // ===== FOOTER =====
        Label footerLabel = new Label("© 2026 Construction Warehouse System");
        footerLabel.setFont(Font.font("Arial", 11));
        footerLabel.setTextFill(Color.GRAY);

        HBox footer = new HBox(footerLabel);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10));
        footer.setStyle("-fx-background-color: #ecf0f1;");

        // ===== MAIN LAYOUT =====
        VBox root = new VBox(titleBox, formBox, footer);

        Scene scene = new Scene(root, 420, 380);
        stage.setTitle("Login — Warehouse Management System");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void openDashboard(User user, Stage stage) {

        String role = user.getRole().toUpperCase();

        try {
            switch (role) {
                case "ADMIN":
                    new AdminDashboard(user).start(new Stage());
                    break;
                case "STOREKEEPER":
                    new StorekeeperDashboard(user).start(new Stage());
                    break;
                case "SALES":
                    new SalesDashboardV2(user).start(new Stage());
                    break;
                default:
                    new Alert(Alert.AlertType.ERROR, "Unknown role. Access denied.").show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}