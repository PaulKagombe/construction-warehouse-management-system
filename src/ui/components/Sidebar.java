package ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

public class Sidebar extends VBox {

    private final Map<String, Button> menuButtons = new LinkedHashMap<>();

    private MenuSelectionListener listener;
    private String userRole;

    public Sidebar(String userRole) {
        this.userRole = userRole != null ? userRole.toUpperCase() : "ADMIN";

        setPrefWidth(230);
        setSpacing(8);
        setPadding(new Insets(20));

        setStyle(
                "-fx-background-color:#1F2937;"
        );

        createHeader();
        addRoleBasedMenus();

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        getChildren().add(spacer);

        addMenu("Logout");
    }

    private void addRoleBasedMenus() {
        switch (userRole) {
            case "ADMIN":
                addMenu("Dashboard");
                addMenu("Inventory");
                addMenu("Customers");
                addMenu("Suppliers");
                addMenu("Sales");
                addMenu("Reports");
                break;
            case "STOREKEEPER":
                addMenu("Dashboard");
                addMenu("Inventory");
                break;
            case "SALES":
                addMenu("Dashboard");
                addMenu("Inventory");
                addMenu("Sales");
                addMenu("Customers");
                addMenu("Reports");
                break;
            default:
                addMenu("Dashboard");
                addMenu("Reports");
        }
    }

    public String getUserRole() {
        return userRole;
    }

    private void createHeader() {

        Button logo = new Button("WAREHOUSE");

        logo.setDisable(true);

        logo.setStyle(
                "-fx-background-color:transparent;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:22px;" +
                        "-fx-font-weight:bold;"
        );

        getChildren().add(logo);
    }

    private void addMenu(String name) {

        Button button = new Button(name);

        button.setMaxWidth(Double.MAX_VALUE);

        button.setAlignment(Pos.CENTER_LEFT);

        button.setPadding(new Insets(12));

        button.setStyle(defaultStyle());

        button.setOnMouseEntered(e -> {

            if (!button.equals(getActiveButton())) {

                button.setStyle(hoverStyle());

            }

        });

        button.setOnMouseExited(e -> {

            if (!button.equals(getActiveButton())) {

                button.setStyle(defaultStyle());

            }

        });

        button.setOnAction(e -> {

            setActive(name);

            if (listener != null) {

                listener.onMenuSelected(name);

            }

        });

        menuButtons.put(name, button);

        getChildren().add(button);

    }

    private Button activeButton;

    private Button getActiveButton() {

        return activeButton;

    }

    public void setActive(String menu) {

        menuButtons.values().forEach(button ->
                button.setStyle(defaultStyle()));

        activeButton = menuButtons.get(menu);

        if (activeButton != null) {

            activeButton.setStyle(activeStyle());

        }

    }

    public void setMenuSelectionListener(MenuSelectionListener listener) {

        this.listener = listener;

    }

    private String defaultStyle() {

        return """
                -fx-background-color:transparent;
                -fx-text-fill:white;
                -fx-font-size:14px;
                -fx-background-radius:8;
                -fx-cursor:hand;
                """;
    }

    private String hoverStyle() {

        return """
                -fx-background-color:#374151;
                -fx-text-fill:white;
                -fx-font-size:14px;
                -fx-background-radius:8;
                -fx-cursor:hand;
                """;
    }

    private String activeStyle() {

        return """
                -fx-background-color:#2563EB;
                -fx-text-fill:white;
                -fx-font-size:14px;
                -fx-font-weight:bold;
                -fx-background-radius:8;
                """;
    }

    public interface MenuSelectionListener {

        void onMenuSelected(String menu);

    }

}