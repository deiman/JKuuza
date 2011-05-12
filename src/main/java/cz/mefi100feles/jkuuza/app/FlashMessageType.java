package cz.mefi100feles.jkuuza.app;

import java.awt.Color;

/**
 *
 * @author Marek Pilecky
 */
public enum FlashMessageType {

	SUCCESS(Color.GREEN, Color.GREEN),
	ERROR(Color.RED, Color.RED),
	INFO(Color.BLUE, Color.BLUE);

	
	private final Color backgroundColor;
	private final Color foregroundColor;

	FlashMessageType(Color backgroundColor, Color foregroundColor) {
		this.backgroundColor = backgroundColor;
		this.foregroundColor = foregroundColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Color getForegroundColor() {
		return foregroundColor;
	}

	
}
