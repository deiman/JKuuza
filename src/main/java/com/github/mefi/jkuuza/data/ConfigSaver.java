package com.github.mefi.jkuuza.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Marek Pilecky
 */
public class ConfigSaver extends Config {

	public void save(Properties properties) throws IOException {
		FileOutputStream out = null;		

		try {
			out = new FileOutputStream(settingsFile);
			properties.store(out, "No comment.");
		} catch (IOException ex) {
			throw new IOException();
		} finally {
			try {
				out.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
