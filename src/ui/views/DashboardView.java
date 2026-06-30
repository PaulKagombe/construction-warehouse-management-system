package ui.views;

import controller.DashboardController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import ui.components.DashboardCard;

public class DashboardView extends VBox {

    private final DashboardController controller;

    private DashboardCard todaySalesCard;
    private DashboardCard revenueCard;
    private DashboardCard customersCard;
    private DashboardCard totalSalesCard;

    private Timeline timeline;

    public DashboardView() {

        controller = new DashboardController();

        setSpacing(20);
        setPadding(new Insets(20));

        HBox cards = new HBox(20);

        todaySalesCard = new DashboardCard(
                "Today's Sales",
                "",
                "Transactions Today"
        );

        revenueCard = new DashboardCard(
                "Revenue",
                "",
                "Today's Revenue"
        );

        customersCard = new DashboardCard(
                "Customers",
                "",
                "Registered Customers"
        );

        totalSalesCard = new DashboardCard(
                "Total Sales",
                "",
                "All Time"
        );

        cards.getChildren().addAll(
                todaySalesCard,
                revenueCard,
                customersCard,
                totalSalesCard
        );

        getChildren().add(cards);

        // Load dashboard immediately
        refreshDashboard();

        // Auto refresh every 30 seconds
        timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(30),
                        e -> refreshDashboard()
                )
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void refreshDashboard() {

        todaySalesCard.setValue(
                String.valueOf(controller.getTodaySales())
        );

        revenueCard.setValue(
                String.format("KES %,.2f", controller.getTodayRevenue())
        );

        customersCard.setValue(
                String.valueOf(controller.getTotalCustomers())
        );

        totalSalesCard.setValue(
                String.valueOf(controller.getTotalSales())
        );
    }
}