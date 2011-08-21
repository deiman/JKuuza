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

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * FlashMessage consists of a message text, type and icon. Type is set by FlashMessageType Enum.
 *
 * @author Marek Pilecky
 */
public class FlashMessage {

	private FlashMessageType type;
	private String text;
	private Icon icon;
	private JLabel jLabel;

	public FlashMessage(String text, FlashMessageType type, Icon icon) {
		this.text = text;
		this.type = type;
		this.icon = icon;
		construct(text, type, icon);
	}

	/**
	 * Creates JLabel instance with message text and icon
	 * 
	 * @param text message text
	 * @param type type of message
	 * @param icon icon of message
	 */
	private void construct(String text, FlashMessageType type, Icon icon) {
		this.jLabel = new JLabel(text, icon, JLabel.RIGHT);
		this.jLabel.setForeground(type.getForegroundColor());
		this.jLabel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
	}

	/**
	 * Returns JLabel with FlashMessage. Can be displayed in appropriate panel.
	 *
	 * @return JLabel
	 */
	public JLabel getJLabel() {
		return this.jLabel;
	}

	/**
	 * Returns text of FlashMessage
	 *
	 * @return
	 */
	public String getText() {
		return text;
	}

	/**
	 * Set text to FlashMessage
	 *
	 * @param text Text of FlashMessage
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Return type of FlashMessage
	 *
	 * @return Enum with type of message
	 */
	public FlashMessageType getType() {
		return type;
	}

	/**
	 * Set type of FlashMessage
	 *
	 * @param type Enum with type
	 */
	public void setType(FlashMessageType type) {
		this.type = type;
	}

	/**
	 * @return string with type and text of message [type]: text
	 */
	@Override
	public String toString() {
		return "[" + type + "]: " + text;
	}
}
