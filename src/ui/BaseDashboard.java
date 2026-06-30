package ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import model.User;
import ui.components.Sidebar;
import ui.components.TopBar;
import ui.views.CustomerView;
import ui.views.DashboardView;
import util.AlertHelper;
import util.SessionManager;

public abstract class BaseDashboard {

    protected final User user;

    protected BorderPane root;
    protected StackPane contentPane;

    protected Sidebar sidebar;
    protected TopBar topBar;
    protected Stage currentStage;

    public BaseDashboard(User user) {
        this.user = user;
    }

    public void start(Stage stage) {

        currentStage = stage;
        root = new BorderPane();

        root.setStyle("-fx-background-color:#F5F7FA;");

        sidebar = new Sidebar(user.getRole());
        sidebar.setMenuSelectionListener(menu -> {

            switch (menu) {

                case "Dashboard" -> {

                    topBar.setTitle("Dashboard");

                    contentPane.getChildren().setAll(
                            new DashboardView()
                    );

                }
                case "Customers" -> {

                    topBar.setTitle("Customers");

                    contentPane.getChildren().setAll(
                            new CustomerView()
                    );

                }

                case "Logout" -> {

                    if (AlertHelper.confirm(
                            "Logout",
                            "Are you sure you want to logout?"
                    )) {

                        stage.close();

                        SessionManager.logout();

                        new LoginScreen().start(new Stage());

                    }

                }

                default -> {

                    topBar.setTitle(menu);

                    contentPane.getChildren().setAll(
                            new Label(menu + " Module Coming Soon")
                    );

                }

            }

        });

        topBar = new TopBar(user);

        contentPane = new StackPane();
        contentPane.setPadding(new Insets(20));

        root.setLeft(sidebar);
        root.setTop(topBar);
        root.setCenter(contentPane);
        contentPane.getChildren().add(new DashboardView());

        Scene scene = new Scene(root, 1400, 800);

        stage.setTitle("Warehouse Management System");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        initialize();
    }

    /**
     * Every dashboard (Admin, Sales, Storekeeper)
     * will implement this method.
     */
    protected abstract void initialize();
}