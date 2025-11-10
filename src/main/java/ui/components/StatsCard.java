package ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import util.ColorConstants;

import java.util.function.Supplier;

/**
 * Reusable statistics card component for dashboard
 */
public class StatsCard extends VBox {
    private final Label titleLabel;
    private final Label valueLabel;
    private final Label subtitleLabel;
    private final Supplier<String> valueSupplier;
    private final Supplier<String> subtitleSupplier;

    public StatsCard(String title, String icon, Supplier<String> valueSupplier, Supplier<String> subtitleSupplier) {
        this.valueSupplier = valueSupplier;
        this.subtitleSupplier = subtitleSupplier;

        getStyleClass().add("stats-card");
        setPadding(new Insets(20));
        setSpacing(8);
        setAlignment(Pos.TOP_LEFT);
        setMinWidth(200);
        setMaxWidth(300);

        // Title with icon
        titleLabel = new Label(icon + "  " + title);
        titleLabel.getStyleClass().add("stats-card-title");
        titleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + ColorConstants.TEXT_SECONDARY + ";");

        // Value
        valueLabel = new Label(valueSupplier.get());
        valueLabel.getStyleClass().add("stats-card-value");
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + ColorConstants.TEXT_PRIMARY + ";");

        // Subtitle
        subtitleLabel = new Label(subtitleSupplier.get());
        subtitleLabel.getStyleClass().add("stats-card-subtitle");
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ColorConstants.TEXT_SECONDARY + ";");

        getChildren().addAll(titleLabel, valueLabel, subtitleLabel);

        // Apply card styling
        setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 14px; " +
            "-fx-border-color: rgba(0, 0, 0, 0.08); " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 14px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );
    }

    /**
     * Refresh the card values
     */
    public void refresh() {
        valueLabel.setText(valueSupplier.get());
        subtitleLabel.setText(subtitleSupplier.get());
    }
}
