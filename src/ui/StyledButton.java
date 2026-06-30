package ui;

import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Reusable styled button component with consistent theming.
 */
public class StyledButton extends Button {

	// Color scheme constants
	public static final String PRIMARY_COLOR = "#2c3e50";
	public static final String PRIMARY_HOVER = "#34495e";
	public static final String SUCCESS_COLOR = "#27ae60";
	public static final String SUCCESS_HOVER = "#229954";
	public static final String DANGER_COLOR = "#e74c3c";
	public static final String DANGER_HOVER = "#c0392b";
	public static final String WARNING_COLOR = "#f39c12";
	public static final String WARNING_HOVER = "#d68910";
	public static final String SECONDARY_COLOR = "#95a5a6";
	public static final String SECONDARY_HOVER = "#7f8c8d";

	private String normalColor;
	private String hoverColor;

	/**
	 * Create a button with primary styling (default).
	 */
	public StyledButton(String text) {
		this(text, PRIMARY_COLOR, PRIMARY_HOVER);
	}

	/**
	 * Create a button with custom styling.
	 */
	public StyledButton(String text, String normalColor, String hoverColor) {
		super(text);
		this.normalColor = normalColor;
		this.hoverColor = hoverColor;
		applyStyle();
		setupHoverEffect();
	}

	/**
	 * Create a primary button (blue).
	 */
	public static StyledButton primary(String text) {
		return new StyledButton(text, PRIMARY_COLOR, PRIMARY_HOVER);
	}

	/**
	 * Create a success button (green).
	 */
	public static StyledButton success(String text) {
		return new StyledButton(text, SUCCESS_COLOR, SUCCESS_HOVER);
	}

	/**
	 * Create a danger button (red).
	 */
	public static StyledButton danger(String text) {
		return new StyledButton(text, DANGER_COLOR, DANGER_HOVER);
	}

	/**
	 * Create a warning button (orange).
	 */
	public static StyledButton warning(String text) {
		return new StyledButton(text, WARNING_COLOR, WARNING_HOVER);
	}

	/**
	 * Create a secondary button (gray).
	 */
	public static StyledButton secondary(String text) {
		return new StyledButton(text, SECONDARY_COLOR, SECONDARY_HOVER);
	}

	private void applyStyle() {
		setPrefHeight(40);
		setPrefWidth(100);
		setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
		setStyle(String.format("""
				-fx-background-color: %s;
				-fx-text-fill: white;
				-fx-background-radius: 6;
				-fx-padding: 10 20;
				-fx-cursor: hand;
				-fx-font-size: 13;
				""", normalColor));
	}

	private void setupHoverEffect() {
		setOnMouseEntered(e -> setStyle(String.format("""
				-fx-background-color: %s;
				-fx-text-fill: white;
				-fx-background-radius: 6;
				-fx-padding: 10 20;
				-fx-cursor: hand;
				-fx-font-size: 13;
				-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 8, 0, 0, 2);
				""", hoverColor)));

		setOnMouseExited(e -> setStyle(String.format("""
				-fx-background-color: %s;
				-fx-text-fill: white;
				-fx-background-radius: 6;
				-fx-padding: 10 20;
				-fx-cursor: hand;
				-fx-font-size: 13;
				""", normalColor)));
	}
}
