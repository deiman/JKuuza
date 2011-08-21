/*
 *   Copyright 2011 Marek Pilecky
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
