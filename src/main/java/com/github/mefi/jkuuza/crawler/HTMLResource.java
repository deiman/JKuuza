package com.github.mefi.jkuuza.crawler;

import org.niocchi.core.MemoryResource;

/**
 *
 * @author Marek Pilecky
 */
public class HTMLResource extends MemoryResource {

	/**
	 * Returns true if this resource is a valid HTML page.
	 * @return true if this resource is a valid HTML page.
	 */
	@Override
	public boolean isValid() {
		String contentType = getHeader("Content-Type");
		if (contentType == null) {
			return false;
		}
		contentType = contentType.toLowerCase().trim();
		return ((contentType.indexOf("text/html") == 0
			|| contentType.indexOf("application/xhtml+xml") == 0));
	}
}
