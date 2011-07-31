package com.github.mefi.jkuuza.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Marek Pilecky
 */
public class ConfigLoader extends Config {

	public Properties load() throws IOException {
		Properties properties = new Properties();
		FileInputStream in = null;

		try {
			in = new FileInputStream(settingsFile);
			properties.load(in);
		} catch (IOException ex1) {
			try {
				in = new FileInputStream(defaultFile);
				properties.load(in);
			} catch (IOException ex2) {
				 throw new IOException();
			}
		}
		return properties;
	}
}
