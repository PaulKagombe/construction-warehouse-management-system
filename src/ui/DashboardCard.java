package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DashboardCard extends VBox {

    private final Label titleLabel;
    private final Label valueLabel;
    private final Label subtitleLabel;

    public DashboardCard(String title,
                         String value,
                         String subtitle) {

        setPrefSize(220, 120);
        setMinSize(220, 120);
        setMaxSize(220, 120);

        setSpacing(10);
        setPadding(new Insets(20));
        setAlignment(Pos.CENTER_LEFT);

        setBackground(new Background(
                new BackgroundFill(
                        Color.WHITE,
                        new CornerRadii(12),
                        Insets.EMPTY
                )
        ));

        setBorder(new Border(
                new BorderStroke(
                        Color.web("#E5E7EB"),
                        BorderStrokeStyle.SOLID,
                        new CornerRadii(12),
                        new BorderWidths(1)
                )
        ));

        titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        titleLabel.setTextFill(Color.web("#6B7280"));

        valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.web("#111827"));

        subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        subtitleLabel.setTextFill(Color.web("#10B981"));

        getChildren().addAll(
                titleLabel,
                valueLabel,
                subtitleLabel
        );

        initializeHoverEffect();
    }

    private void initializeHoverEffect() {

        setOnMouseEntered(e -> {

            setScaleX(1.03);
            setScaleY(1.03);

        });

        setOnMouseExited(e -> {

            setScaleX(1.0);
            setScaleY(1.0);

        });
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }
}