package com.github.mefi.jkuuza.gui.model;

import java.awt.Color;

/**
 * @author Marek Pilecky
 */
public enum FlashMessageType {

	SUCCESS(Color.GREEN, Color.GREEN),
	ERROR(Color.RED, Color.RED),
	INFO(Color.BLUE, Color.BLUE);
	private final Color backgroundColor;
	private final Color foregroundColor;

	/**
	 * Create new FlashmessageType
	 * @param backgroundColor Color of message background
	 * @param foregroundColor Color of message foreground (font)
	 */
	FlashMessageType(Color backgroundColor, Color foregroundColor) {
		this.backgroundColor = backgroundColor;
		this.foregroundColor = foregroundColor;
	}

	/**
	 * Returns background color
	 * @return Color
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * Returns foreground (font) color
	 * @return Color
	 */
	public Color getForegroundColor() {
		return foregroundColor;
	}
}
