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

package com.github.mefi.jkuuza.crawler.gui;

import com.github.mefi.jkuuza.gui.AppView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author Marek Pilecky
 */
public class CrawlerConsole {

	private final static String newline = "\n";
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";


	/**
	 * Appends text into new line of crawler console JTextArea
	 * 
	 * @param text
	 */
	public static void print(String text) {
		JTextArea console = AppView.getInstance().getCrawlerConsole();
		console.insert(text + newline, 0);		
	}

	/**
	 * Appends text into new line of crawler console JTextArea
	 *
	 * @param text
	 * @param withDate if true, line starts with current date
	 */
	public static void print(String text, boolean withDate) {
		if (withDate) {
			String date = getFormatedDate();
			text = "[" + date + "] - " + text;
		}
		print(text);
	}

	/**
	 * Prints current Date into new line of crawler console JTextArea
	 */
	public static void printDate() {
		String date = getFormatedDate();
		print(date);
	}

	/**
	 * Prints new line into crawler console JTextArea
	 */
	public static void printNewLine() {
		print("");
	}

	/**
	 * Gets current date and formates it
	 *
	 * @return String with formated date
	 */
	private static String getFormatedDate() {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_NOW);

		return dateFormat.format(calendar.getTime());
	}
	






}
