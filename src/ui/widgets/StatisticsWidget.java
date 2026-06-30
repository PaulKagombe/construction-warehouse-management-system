package ui.widgets;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import ui.components.DashboardCard;

public class StatisticsWidget extends HBox {

    private final DashboardCard salesCard;
    private final DashboardCard revenueCard;
    private final DashboardCard customerCard;
    private final DashboardCard stockCard;

    public StatisticsWidget() {

        setSpacing(20);

        setPadding(new Insets(15));

        salesCard = new DashboardCard(
                "Today's Sales",
                "0",
                "Transactions"
        );

        revenueCard = new DashboardCard(
                "Revenue",
                "KSh 0",
                "This Month"
        );

        customerCard = new DashboardCard(
                "Customers",
                "0",
                "Registered"
        );

        stockCard = new DashboardCard(
                "Inventory",
                "0",
                "Items"
        );

        getChildren().addAll(
                salesCard,
                revenueCard,
                customerCard,
                stockCard
        );

    }

    public void updateSales(String value) {

        salesCard.setValue(value);

    }

    public void updateRevenue(String value) {

        revenueCard.setValue(value);

    }

    public void updateCustomers(String value) {

        customerCard.setValue(value);

    }

    public void updateStock(String value) {

        stockCard.setValue(value);

    }

}