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
